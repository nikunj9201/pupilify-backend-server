package com.smartschool.api.serviceImpl;

import com.smartschool.api.dto.BusFeeRateStructureDTO;
import com.smartschool.api.dto.StudentMonthlyFeeStructureDTO;
import com.smartschool.api.dto.StudentBusAssignmentRequestDTO;
import com.smartschool.api.dto.TransportFeeLogDTO;
import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.BusMonthlyFeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusMonthlyFeeServiceImpl implements BusMonthlyFeeService {

    @Autowired
    private BusFeeRateStructureRepository feeRateRepository;

    @Autowired
    private StudentMonthlyFeeStructureRepository studentFeeRepository;

    @Autowired
    private StoppageRepository stoppageRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AcademicYearConfigRepository academicYearRepository;

    @Autowired
    private TransportFeeLogRepository transportFeeLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BusFeeRateStructure createFeeRateStructure(Long stoppageId, Long busId, Long schoolId, Long academicYearId,
                                                      double monthlyFeeAmount, int totalMonths, String academicYearStartMonth) {
        Stoppage stoppage = stoppageRepository.findById(stoppageId).orElseThrow(() -> new RuntimeException("Stoppage not found"));
        Bus bus = busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found"));
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new RuntimeException("School not found"));
        AcademicYearConfig academicYear = academicYearRepository.findById(academicYearId).orElseThrow(() -> new RuntimeException("Academic year not found"));

        BusFeeRateStructure structure = new BusFeeRateStructure();
        structure.setStoppage(stoppage);
        structure.setBus(bus);
        structure.setSchool(school);
        structure.setAcademicYear(academicYear);
        structure.setMonthlyFeeAmount(monthlyFeeAmount);
        structure.setTotalMonths(totalMonths);
        structure.setAcademicYearStartMonth(academicYearStartMonth);
        structure.setActive(true);
        return feeRateRepository.save(structure);
    }

    @Override
    public BusFeeRateStructure updateFeeRateStructure(Long rateStructureId, double monthlyFeeAmount) {
        BusFeeRateStructure structure = feeRateRepository.findById(rateStructureId).orElseThrow(() -> new RuntimeException("Fee rate structure not found"));
        structure.setMonthlyFeeAmount(monthlyFeeAmount);
        return feeRateRepository.save(structure);
    }

    @Override
    public BusFeeRateStructure getFeeRateStructure(Long stoppageId, Long busId, Long schoolId, Long academicYearId) {
        return feeRateRepository.findByStoppageIdAndBusIdAndSchoolIdAndAcademicYearId(stoppageId, busId, schoolId, academicYearId)
                .orElseThrow(() -> new RuntimeException("Fee rate structure not found"));
    }

    @Override
    public List<BusFeeRateStructureDTO> getFeeRateStructuresByBus(Long busId, Long schoolId, Long academicYearId) {
        return feeRateRepository.findByBusIdAndSchoolIdAndAcademicYearIdAndActiveTrue(busId, schoolId, academicYearId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFeeRateStructure(Long rateStructureId) {
        BusFeeRateStructure structure = feeRateRepository.findById(rateStructureId).orElseThrow(() -> new RuntimeException("Fee rate structure not found"));
        feeRateRepository.delete(structure);
    }

    @Override
    public StudentMonthlyFeeStructure assignStudentMonthlyFees(StudentBusAssignmentRequestDTO request) {
        Student student = studentRepository.findById(request.getStudentId()).orElseThrow(() -> new RuntimeException("Student not found"));
        Stoppage stoppage = stoppageRepository.findById(request.getStoppageId()).orElseThrow(() -> new RuntimeException("Stoppage not found"));
        Bus bus = busRepository.findById(request.getBusId()).orElseThrow(() -> new RuntimeException("Bus not found"));
        School school = schoolRepository.findById(request.getSchoolId()).orElseThrow(() -> new RuntimeException("School not found"));
        AcademicYearConfig academicYear = academicYearRepository.findById(request.getAcademicYearId()).orElseThrow(() -> new RuntimeException("Academic year not found"));

        // Check if already assigned
        studentFeeRepository.findByStudentIdAndStoppageIdAndAcademicYearId(request.getStudentId(), request.getStoppageId(), request.getAcademicYearId())
                .ifPresent(existing -> {
                    throw new RuntimeException("Student is already assigned to this stoppage in this academic year");
                });

        // Get fee rate structure
        BusFeeRateStructure rateStructure = getFeeRateStructure(request.getStoppageId(), request.getBusId(), request.getSchoolId(), request.getAcademicYearId());

        // Defensive handling for selected months and payment frequency to avoid runtime exceptions
        List<String> selectedMonths = request.getSelectedMonths() == null ? new ArrayList<>() : request.getSelectedMonths();
        String paymentFreqStr = request.getPaymentFrequency();
        StudentMonthlyFeeStructure.PaymentFrequency paymentFreq = StudentMonthlyFeeStructure.PaymentFrequency.MONTHLY;
        if (paymentFreqStr != null) {
            try {
                paymentFreq = StudentMonthlyFeeStructure.PaymentFrequency.valueOf(paymentFreqStr);
            } catch (IllegalArgumentException e) {
                // Invalid value provided, default to MONTHLY (could also log a warning)
                paymentFreq = StudentMonthlyFeeStructure.PaymentFrequency.MONTHLY;
            }
        }

        // Calculate total fee
        double totalFee = calculateTotalMonthlyFees(selectedMonths, request.getJoiningMonth(), 
                rateStructure.getMonthlyFeeAmount(), rateStructure.getTotalMonths());

        StudentMonthlyFeeStructure feeStructure = new StudentMonthlyFeeStructure();
        feeStructure.setStudent(student);
        feeStructure.setStoppage(stoppage);
        feeStructure.setBus(bus);
        feeStructure.setSchool(school);
        feeStructure.setAcademicYear(academicYear);
        feeStructure.setMonthlyFeeAmount(rateStructure.getMonthlyFeeAmount());
        feeStructure.setSelectedMonths(convertListToJson(selectedMonths));
        feeStructure.setJoiningMonth(request.getJoiningMonth());
        // Extract year from currentYear (e.g., "2026-27" -> 2026)
        int yearValue = Integer.parseInt(academicYear.getCurrentYear().split("-")[0]);
        feeStructure.setJoiningYear(yearValue);
        feeStructure.setPaymentFrequency(paymentFreq);
        feeStructure.setTotalFeeAmount(totalFee);
        feeStructure.setAssignmentDate(LocalDate.now());
        feeStructure.setActive(true);

        return studentFeeRepository.save(feeStructure);
    }

    @Override
    public StudentMonthlyFeeStructure updateStudentFeeAssignment(Long assignmentId, List<String> selectedMonths, String paymentFrequency) {
        StudentMonthlyFeeStructure feeStructure = studentFeeRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        double totalFee = calculateTotalMonthlyFees(selectedMonths, feeStructure.getJoiningMonth(), 
                feeStructure.getMonthlyFeeAmount(), 12);

        feeStructure.setSelectedMonths(convertListToJson(selectedMonths));
        feeStructure.setPaymentFrequency(StudentMonthlyFeeStructure.PaymentFrequency.valueOf(paymentFrequency));
        feeStructure.setTotalFeeAmount(totalFee);
        return studentFeeRepository.save(feeStructure);
    }

    @Override
    public StudentMonthlyFeeStructureDTO getStudentFeeAssignment(Long studentId, Long academicYearId) {
        return studentFeeRepository.findByStudentIdAndAcademicYearIdAndActiveTrue(studentId, academicYearId)
                .stream()
                .findFirst()
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("No fee assignment found for this student"));
    }

    @Override
    public List<StudentMonthlyFeeStructureDTO> getStudentFeeAssignmentsByBus(Long busId, Long schoolId, Long academicYearId) {
        return studentFeeRepository.findByBusIdAndSchoolIdAndAcademicYearIdAndActiveTrue(busId, schoolId, academicYearId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentMonthlyFeeStructureDTO> getStudentFeeAssignmentsByStoppage(Long stoppageId, Long academicYearId) {
        return studentFeeRepository.findByStoppageIdAndAcademicYearIdAndActiveTrue(stoppageId, academicYearId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentMonthlyFeeStructureDTO> getStudentFeeHistory(Long studentId) {
        return studentFeeRepository.findByStudentId(studentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransportFeeLogDTO> getStudentPaymentHistory(Long studentId) {
        return transportFeeLogRepository.findByStudentId(studentId)
                .stream()
                .map(TransportFeeLogDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStudentFeeAssignment(Long assignmentId) {
        StudentMonthlyFeeStructure feeStructure = studentFeeRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Assignment not found"));
        feeStructure.setActive(false);
        studentFeeRepository.save(feeStructure);
    }

    @Override
    public List<Map<String, Object>> getMonthlyFeesDueReport(Long schoolId, Long academicYearId) {
        List<StudentMonthlyFeeStructure> assignments = studentFeeRepository.findBySchoolIdAndAcademicYearIdAndActiveTrue(schoolId, academicYearId);
        List<Map<String, Object>> report = new ArrayList<>();

        for (StudentMonthlyFeeStructure assignment : assignments) {
            Map<String, Object> row = new HashMap<>();
            row.put("studentId", assignment.getStudent().getId());
            row.put("studentName", assignment.getStudent().getName());
            row.put("enrollmentId", assignment.getStudent().getEnrollmentId());
            row.put("stoppage", assignment.getStoppage().getStopName());
            row.put("monthlyFeeAmount", assignment.getMonthlyFeeAmount());
            row.put("selectedMonths", convertJsonToList(assignment.getSelectedMonths()));
            row.put("joiningMonth", assignment.getJoiningMonth());
            row.put("totalMonthsSelected", convertJsonToList(assignment.getSelectedMonths()).size());
            row.put("totalFeeAmount", assignment.getTotalFeeAmount());
            row.put("paymentFrequency", assignment.getPaymentFrequency());
            report.add(row);
        }
        return report;
    }

    @Override
    public List<Map<String, Object>> getStudentMonthlyFeesBreakdown(Long studentId, Long academicYearId) {
        List<StudentMonthlyFeeStructure> assignments = studentFeeRepository.findByStudentIdAndAcademicYearIdAndActiveTrue(studentId, academicYearId);
        List<Map<String, Object>> breakdown = new ArrayList<>();

        for (StudentMonthlyFeeStructure assignment : assignments) {
            List<String> months = convertJsonToList(assignment.getSelectedMonths());
            for (String month : months) {
                Map<String, Object> row = new HashMap<>();
                row.put("month", month);
                row.put("feeAmount", assignment.getMonthlyFeeAmount());
                row.put("stoppage", assignment.getStoppage().getStopName());
                row.put("status", "DUE");
                breakdown.add(row);
            }
        }
        return breakdown;
    }

    @Override
    public double calculateTotalMonthlyFees(List<String> selectedMonths, String joiningMonth, double monthlyFeeAmount, int academicYearTotalMonths) {
        if (selectedMonths == null || selectedMonths.isEmpty()) {
            return 0.0;
        }
        // Conservative calculation: count selected months only. If joiningMonth is provided,
        // ensure we do not count months before joining (caller should provide selectedMonths accordingly).
        int totalMonthsForFee = selectedMonths.size();
        return totalMonthsForFee * monthlyFeeAmount;
    }

    private BusFeeRateStructureDTO convertToDTO(BusFeeRateStructure structure) {
        BusFeeRateStructureDTO dto = new BusFeeRateStructureDTO();
        dto.setId(structure.getId());
        dto.setStoppageId(structure.getStoppage().getId());
        dto.setStopName(structure.getStoppage().getStopName());
        dto.setBusId(structure.getBus().getId());
        dto.setSchoolId(structure.getSchool().getId());
        dto.setAcademicYearId(structure.getAcademicYear().getId());
        dto.setMonthlyFeeAmount(structure.getMonthlyFeeAmount());
        dto.setTotalMonths(structure.getTotalMonths());
        dto.setAcademicYearStartMonth(structure.getAcademicYearStartMonth());
        dto.setActive(structure.isActive());
        return dto;
    }

    private StudentMonthlyFeeStructureDTO convertToDTO(StudentMonthlyFeeStructure structure) {
        StudentMonthlyFeeStructureDTO dto = new StudentMonthlyFeeStructureDTO();
        dto.setId(structure.getId());
        dto.setStudentId(structure.getStudent().getId());
        dto.setStudentName(structure.getStudent().getName());
        dto.setStoppageId(structure.getStoppage().getId());
        dto.setStopName(structure.getStoppage().getStopName());
        dto.setBusId(structure.getBus().getId());
        dto.setSchoolId(structure.getSchool().getId());
        dto.setAcademicYearId(structure.getAcademicYear().getId());
        dto.setMonthlyFeeAmount(structure.getMonthlyFeeAmount());
        dto.setSelectedMonths(convertJsonToList(structure.getSelectedMonths()));
        dto.setJoiningMonth(structure.getJoiningMonth());
        dto.setJoiningYear(structure.getJoiningYear());
        dto.setPaymentFrequency(structure.getPaymentFrequency().toString());
        dto.setTotalFeeAmount(structure.getTotalFeeAmount());
        dto.setTotalMonthsSelected(convertJsonToList(structure.getSelectedMonths()).size());
        dto.setAssignmentDate(structure.getAssignmentDate().toString());
        dto.setActive(structure.isActive());
        return dto;
    }

    private String convertListToJson(List<String> list) {
        try {
            if (list == null) return "[]";
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> convertJsonToList(String json) {
        try {
            if (json == null || json.trim().isEmpty() || json.equalsIgnoreCase("null")) return new ArrayList<>();
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

