package com.smartschool.api.serviceImpl;

import com.smartschool.api.dto.BusAssignmentDTO;
import com.smartschool.api.dto.TransportFeeLogDTO;
import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        return busRepository.findBySchoolId(schoolId);
    }

    @Override
    public StudentBusAssignment assignStudentToBus(Long studentId, Long stoppageId, Long academicYearId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        Stoppage stoppage = stoppageRepository.findById(stoppageId).orElseThrow(() -> new RuntimeException("Stoppage not found"));
        AcademicYearConfig academicYear = academicYearRepository.findById(academicYearId).orElseThrow(() -> new RuntimeException("Academic year not found"));

        assignmentRepository.findByStudentIdAndAcademicYearIdAndIsActiveTrue(studentId, academicYearId).ifPresent(assignment -> {
            throw new RuntimeException("Student is already assigned to a bus");
        });

        StudentBusAssignment newAssignment = new StudentBusAssignment();
        newAssignment.setStudent(student);
        newAssignment.setStoppage(stoppage);
        newAssignment.setAcademicYear(academicYear);
        newAssignment.setTransportFee(stoppage.getFee());
        newAssignment.setActive(true);

        return assignmentRepository.save(newAssignment);
    }

    @Override
    public void generateMonthlyFees(Long schoolId, Long academicYearId, List<String> months) {
        List<StudentBusAssignment> assignments = assignmentRepository.findByStudent_School_IdAndAcademicYearIdAndIsActiveTrue(schoolId, academicYearId);
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
        TransportFeeLog dueLog = feeLogRepository.findFirstByStudentIdAndAcademicYearIdAndStatusOrderByMonthYearAsc(studentId, academicYearId, TransportFeeLog.FeeStatus.DUE)
                .orElseThrow(() -> new RuntimeException("No due bus fee found for this student"));

        dueLog.setAmountPaid(amount);
        dueLog.setPaymentMode(paymentMode);
        dueLog.setPaymentDate(LocalDate.now());
        dueLog.setStatus(TransportFeeLog.FeeStatus.PAID);
        TransportFeeLog savedLog = feeLogRepository.save(dueLog);
        return TransportFeeLogDTO.fromEntity(savedLog);
    }

    @Override
    public List<Map<String, Object>> getBusFeeDueReport(Long schoolId, Long academicYearId) {
        List<StudentBusAssignment> assignments = assignmentRepository.findByStudent_School_IdAndAcademicYearIdAndIsActiveTrue(schoolId, academicYearId);
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
        return assignmentRepository.findByStudentIdAndAcademicYearIdAndIsActiveTrue(studentId, academicYearId)
                .orElseThrow(() -> new RuntimeException("No active bus assignment found for this student in the given academic year"));
    }

    @Override
    public List<BusAssignmentDTO> getAssignmentsByRoute(Long routeId) {
        return assignmentRepository.findByStoppage_Route_Id(routeId).stream()
                .map(assignment -> {
                    BusAssignmentDTO dto = new BusAssignmentDTO();
                    dto.setAssignmentId(assignment.getId());
                    dto.setStudentId(assignment.getStudent().getId());
                    dto.setEnrollmentId(assignment.getStudent().getEnrollmentId());
                    dto.setName(assignment.getStudent().getName());
                    dto.setPhoneNo(assignment.getStudent().getPhoneNo());
                    dto.setStopName(assignment.getStoppage().getStopName());
                    dto.setFee(assignment.getStoppage().getFee());
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

        if (driverCount > 0 || routeCount > 0) {
            throw new RuntimeException("Cannot delete bus. It has " + driverCount + " driver(s) and " + routeCount + " route(s) assigned. Please re-assign or delete them first.");
        }

        busRepository.delete(bus);
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