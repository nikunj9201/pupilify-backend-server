package com.smartschool.api.serviceImpl;

import com.smartschool.api.dto.BusAssignmentDTO;
import com.smartschool.api.dto.TransportFeeLogDTO;
import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BusServiceImpl implements BusService {

    @Autowired
    private StudentBusAssignmentRepository assignmentRepository;

    @Autowired
    private TransportFeeLogRepository feeLogRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StoppageRepository stoppageRepository;

    @Autowired
    private AcademicYearConfigRepository academicYearRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private com.smartschool.api.repository.StudentMonthlyFeeStructureRepository studentMonthlyFeeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Bus addBus(Long schoolId, String registrationNo, int capacity) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new RuntimeException("School not found"));
        Bus bus = new Bus();
        bus.setSchool(school);
        bus.setRegistrationNo(registrationNo);
        bus.setCapacity(capacity);
        return busRepository.save(bus);
    }

    @Override
    public List<Bus> getBusesBySchool(Long schoolId) {
        // return only active buses by default
        return busRepository.findBySchoolIdAndActiveTrue(schoolId);
    }

    @Override
    public StudentBusAssignment assignStudentToBus(Long studentId, Long stoppageId, Long academicYearId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        Stoppage stoppage = stoppageRepository.findById(stoppageId).orElseThrow(() -> new RuntimeException("Stoppage not found"));
        AcademicYearConfig academicYear = academicYearRepository.findById(academicYearId).orElseThrow(() -> new RuntimeException("Academic year not found"));

        assignmentRepository.findByStudentIdAndAcademicYearIdAndActiveTrue(studentId, academicYearId).ifPresent(assignment -> {
            throw new RuntimeException("Student is already assigned to a bus");
        });

        StudentBusAssignment newAssignment = new StudentBusAssignment();
        newAssignment.setStudent(student);
        newAssignment.setStoppage(stoppage);
        newAssignment.setAcademicYear(academicYear);
        //newAssignment.setTransportFee(stoppage.getFee());
        newAssignment.setActive(true);

        return assignmentRepository.save(newAssignment);
    }

    @Override
    public void generateMonthlyFees(Long schoolId, Long academicYearId, List<String> months) {
        List<StudentBusAssignment> assignments = assignmentRepository.findByStudent_School_IdAndAcademicYearIdAndActiveTrue(schoolId, academicYearId);
        AcademicYearConfig academicYear = academicYearRepository.findById(academicYearId).orElseThrow(() -> new RuntimeException("Academic year not found"));

        List<TransportFeeLog> logs = new ArrayList<>();
        for (StudentBusAssignment assignment : assignments) {
            for (String month : months) {
                TransportFeeLog log = new TransportFeeLog();
                log.setStudent(assignment.getStudent());
                log.setAcademicYear(academicYear);
                log.setAmountDue(assignment.getTransportFee());
                log.setMonthYear(month + " " + LocalDate.now().getYear());
                logs.add(log);
            }
        }
        feeLogRepository.saveAll(logs);
    }

    @Override
    public TransportFeeLogDTO collectBusFee(Long studentId, Long academicYearId, double amount, String paymentMode) {
        // First try to find an existing TransportFeeLog with DUE status
        java.util.Optional<TransportFeeLog> dueOptional = feeLogRepository.findFirstByStudentIdAndAcademicYearIdAndStatusOrderByMonthYearAsc(studentId, academicYearId, TransportFeeLog.FeeStatus.DUE);
        if (dueOptional.isPresent()) {
            TransportFeeLog dueLog = dueOptional.get();
            dueLog.setAmountPaid(amount);
            dueLog.setPaymentMode(paymentMode);
            dueLog.setPaymentDate(LocalDate.now());
            dueLog.setStatus(TransportFeeLog.FeeStatus.PAID);
            TransportFeeLog savedLog = feeLogRepository.save(dueLog);
            return TransportFeeLogDTO.fromEntity(savedLog);
        }

        // Fallback: maybe fees are managed by the newer monthly-fee assignment (StudentMonthlyFeeStructure)
        // Try to find a monthly assignment and create a paid TransportFeeLog on-the-fly so collection succeeds.
        java.util.List<StudentMonthlyFeeStructure> assignments = studentMonthlyFeeRepository.findByStudentIdAndAcademicYearIdAndActiveTrue(studentId, academicYearId);
        if (!assignments.isEmpty()) {
            StudentMonthlyFeeStructure assignment = assignments.get(0);
            java.util.List<String> months = new ArrayList<>();
            try {
                if (assignment.getSelectedMonths() != null) {
                    months = objectMapper.readValue(assignment.getSelectedMonths(), new TypeReference<java.util.List<String>>() {});
                }
            } catch (Exception e) {
                // ignore parsing error and proceed with empty months
            }

            if (months.isEmpty()) {
                throw new RuntimeException("No due months for this student");
            }

            int year = assignment.getJoiningYear() > 0 ? assignment.getJoiningYear() : LocalDate.now().getYear();
            double monthlyFee = assignment.getMonthlyFeeAmount();
            double remainingAmount = amount;
            TransportFeeLog lastLog = null;

            // Create payment logs for each month, distributing the payment amount
            java.util.List<String> paidMonths = new ArrayList<>();
            for (String month : months) {
                if (remainingAmount <= 0) break;

                TransportFeeLog newLog = new TransportFeeLog();
                newLog.setStudent(assignment.getStudent());
                newLog.setAcademicYear(assignment.getAcademicYear());
                newLog.setMonthYear(month + " " + year);
                newLog.setAmountDue(monthlyFee);
                
                // Allocate payment: either the remaining amount or the full month's fee
                double amountForThisMonth = Math.min(remainingAmount, monthlyFee);
                newLog.setAmountPaid(amountForThisMonth);
                newLog.setPaymentMode(paymentMode);
                newLog.setPaymentDate(LocalDate.now());
                
                // Determine status based on payment
                if (amountForThisMonth >= monthlyFee) {
                    newLog.setStatus(TransportFeeLog.FeeStatus.PAID);
                    paidMonths.add(month);
                } else {
                    newLog.setStatus(TransportFeeLog.FeeStatus.PARTIALLY_PAID);
                }

                lastLog = feeLogRepository.save(newLog);
                remainingAmount -= amountForThisMonth;
            }

            // Update the monthly-fee assignment to remove paid months and recalculate total fee
            try {
                java.util.List<String> mutableMonths = new ArrayList<>(months);
                mutableMonths.removeAll(paidMonths);

                assignment.setSelectedMonths(objectMapper.writeValueAsString(mutableMonths));
                // Calculate remaining total fee
                double remainingTotal = mutableMonths.size() * monthlyFee;
                assignment.setTotalFeeAmount(Math.max(0.0, remainingTotal));
                if (mutableMonths.isEmpty()) {
                    assignment.setActive(false);
                }
                studentMonthlyFeeRepository.save(assignment);
            } catch (Exception e) {
                // if any error occurs updating the assignment, log and continue (do not fail payment)
                // logging omitted to keep code minimal; in production log the exception
            }

            return TransportFeeLogDTO.fromEntity(lastLog);
        }

        throw new RuntimeException("No due bus fee found for this student");
    }

    @Override
    public Map<String, Object> collectYearlyBusFee(Long studentId, Long academicYearId, double amount, String paymentMode) {
        java.util.List<StudentMonthlyFeeStructure> assignments = studentMonthlyFeeRepository.findByStudentIdAndAcademicYearIdAndActiveTrue(studentId, academicYearId);
        
        if (assignments.isEmpty()) {
            throw new RuntimeException("No monthly fee structure found for this student");
        }

        StudentMonthlyFeeStructure assignment = assignments.get(0);
        java.util.List<String> months = new ArrayList<>();
        try {
            if (assignment.getSelectedMonths() != null) {
                months = objectMapper.readValue(assignment.getSelectedMonths(), new TypeReference<java.util.List<String>>() {});
            }
        } catch (Exception e) {
            // ignore parsing error
        }

        if (months.isEmpty()) {
            throw new RuntimeException("No due months for this student");
        }

        int year = assignment.getJoiningYear() > 0 ? assignment.getJoiningYear() : LocalDate.now().getYear();
        double monthlyFee = assignment.getMonthlyFeeAmount();
        double totalDueAmount = months.size() * monthlyFee;
        
        Map<String, Object> paymentSummary = new HashMap<>();
        paymentSummary.put("studentId", studentId);
        paymentSummary.put("totalMonthsDue", months.size());
        paymentSummary.put("monthlyFeeAmount", monthlyFee);
        paymentSummary.put("totalDueAmount", totalDueAmount);
        paymentSummary.put("amountPaid", amount);
        paymentSummary.put("paymentDate", LocalDate.now().toString());
        paymentSummary.put("paymentMode", paymentMode);

        if (amount < totalDueAmount) {
            paymentSummary.put("status", "PARTIAL_PAYMENT");
            paymentSummary.put("remainingDue", totalDueAmount - amount);
        } else {
            paymentSummary.put("status", "FULL_PAYMENT");
            paymentSummary.put("remainingDue", 0.0);
        }

        java.util.List<Map<String, String>> monthWisePayments = new ArrayList<>();
        double remainingAmount = amount;

        // Create payment logs for each month
        java.util.List<String> paidMonths = new ArrayList<>();
        for (String month : months) {
            if (remainingAmount <= 0) break;

            Map<String, String> monthPayment = new HashMap<>();
            monthPayment.put("month", month);

            TransportFeeLog log = new TransportFeeLog();
            log.setStudent(assignment.getStudent());
            log.setAcademicYear(assignment.getAcademicYear());
            log.setMonthYear(month + " " + year);
            log.setAmountDue(monthlyFee);

            double amountForThisMonth = Math.min(remainingAmount, monthlyFee);
            log.setAmountPaid(amountForThisMonth);
            log.setPaymentMode(paymentMode);
            log.setPaymentDate(LocalDate.now());

            if (amountForThisMonth >= monthlyFee) {
                log.setStatus(TransportFeeLog.FeeStatus.PAID);
                monthPayment.put("status", "PAID");
                paidMonths.add(month);
            } else {
                log.setStatus(TransportFeeLog.FeeStatus.PARTIALLY_PAID);
                monthPayment.put("status", "PARTIALLY_PAID");
            }

            monthPayment.put("amountPaid", String.valueOf(amountForThisMonth));
            monthWisePayments.add(monthPayment);
            feeLogRepository.save(log);
            remainingAmount -= amountForThisMonth;
        }

        // Update the monthly-fee assignment
        try {
            java.util.List<String> mutableMonths = new ArrayList<>(months);
            mutableMonths.removeAll(paidMonths);

            assignment.setSelectedMonths(objectMapper.writeValueAsString(mutableMonths));
            double remainingTotal = mutableMonths.size() * monthlyFee;
            assignment.setTotalFeeAmount(Math.max(0.0, remainingTotal));
            if (mutableMonths.isEmpty()) {
                assignment.setActive(false);
            }
            studentMonthlyFeeRepository.save(assignment);
        } catch (Exception e) {
            // continue even if update fails
        }

        paymentSummary.put("monthWisePayments", monthWisePayments);
        paymentSummary.put("monthsPaid", paidMonths);
        return paymentSummary;
    }

    @Override
    public List<Map<String, Object>> getBusFeeDueReport(Long schoolId, Long academicYearId) {
        List<StudentBusAssignment> assignments = assignmentRepository.findByStudent_School_IdAndAcademicYearIdAndActiveTrue(schoolId, academicYearId);
        List<Map<String, Object>> report = new ArrayList<>();

        for (StudentBusAssignment assignment : assignments) {
            List<TransportFeeLog> dueLogs = feeLogRepository.findByStudentIdAndAcademicYearId(assignment.getStudent().getId(), academicYearId)
                    .stream().filter(log -> log.getStatus() == TransportFeeLog.FeeStatus.DUE).collect(Collectors.toList());

            if (!dueLogs.isEmpty()) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentId", assignment.getStudent().getId());
                row.put("studentName", assignment.getStudent().getName());
                row.put("enrollmentId", assignment.getStudent().getEnrollmentId());
                row.put("className", assignment.getStudent().getSchoolClass().getClassName());
                row.put("sectionName", assignment.getStudent().getSection() != null ? assignment.getStudent().getSection().getSectionName() : "N/A");
                row.put("stoppage", assignment.getStoppage().getStopName());
                row.put("dueMonths", dueLogs.stream().map(TransportFeeLog::getMonthYear).collect(Collectors.toList()));
                row.put("monthlyDueAmount", dueLogs.get(0).getAmountDue());
                row.put("totalYearlyDue", dueLogs.stream().mapToDouble(TransportFeeLog::getAmountDue).sum());
                report.add(row);
            }
        }
        return report;
    }

    @Override
    public StudentBusAssignment getStudentBusAssignment(Long studentId, Long academicYearId) {
        return assignmentRepository.findByStudentIdAndAcademicYearIdAndActiveTrue(studentId, academicYearId)
                .orElseThrow(() -> new RuntimeException("No active bus assignment found for this student in the given academic year"));
    }

    @Override
    public List<BusAssignmentDTO> getAssignmentsByRoute(Long routeId) {
        return assignmentRepository.findByStoppage_Route_IdAndActiveTrue(routeId).stream()
                .map(assignment -> {
                    BusAssignmentDTO dto = new BusAssignmentDTO();
                    dto.setAssignmentId(assignment.getId());
                    dto.setStudentId(assignment.getStudent().getId());
                    dto.setEnrollmentId(assignment.getStudent().getEnrollmentId());
                    dto.setName(assignment.getStudent().getName());
                    dto.setPhoneNo(assignment.getStudent().getPhoneNo());
                    dto.setStopName(assignment.getStoppage().getStopName());
                    //dto.setFee(assignment.getStoppage().getFee());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBus(Long schoolId, Long busId) {
        Bus bus = busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found"));
        if (!bus.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Bus does not belong to this school");
        }

        long driverCount = driverRepository.countByBusId(busId);
        long routeCount = routeRepository.countByBusId(busId);

        // Soft-delete instead of hard delete to avoid FK constraints. Mark bus, its routes and stoppages inactive.
        bus.setActive(false);
        busRepository.save(bus);

        // Deactivate routes and their stoppages for this bus
        java.util.List<Route> routes = routeRepository.findByBusId(busId);
        if (routes != null) {
            for (Route r : routes) {
                r.setActive(false);
                if (r.getStoppages() != null) {
                    for (Stoppage s : r.getStoppages()) {
                        s.setActive(false);
                    }
                }
                routeRepository.save(r);
            }
        }

        // Optionally, if drivers exist, unassign their bus reference instead of failing
        if (driverCount > 0) {
            // set bus reference to null for assigned drivers
            java.util.List<Driver> drivers = driverRepository.findByBusId(busId);
            if (drivers != null) {
                for (Driver d : drivers) {
                    d.setBus(null);
                    driverRepository.save(d);
                }
            }
        }
    }

    @Override
    public List<TransportFeeLogDTO> getFeeHistory(Long studentId, Long academicYearId) {
        return feeLogRepository.findByStudentIdAndAcademicYearId(studentId, academicYearId).stream()
                .map(TransportFeeLogDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void unassignStudent(Long assignmentId) {
        StudentBusAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setActive(false);
        assignmentRepository.save(assignment);

        List<TransportFeeLog> dueLogs = feeLogRepository.findByStudentIdAndAcademicYearId(assignment.getStudent().getId(), assignment.getAcademicYear().getId())
                .stream().filter(log -> log.getStatus() == TransportFeeLog.FeeStatus.DUE).collect(Collectors.toList());
        feeLogRepository.deleteAll(dueLogs);
    }
}