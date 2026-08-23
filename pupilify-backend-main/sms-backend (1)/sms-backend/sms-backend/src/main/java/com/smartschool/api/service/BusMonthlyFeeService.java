package com.smartschool.api.service;

import com.smartschool.api.dto.BusFeeRateStructureDTO;
import com.smartschool.api.dto.StudentMonthlyFeeStructureDTO;
import com.smartschool.api.dto.StudentBusAssignmentRequestDTO;
import com.smartschool.api.dto.TransportFeeLogDTO;
import com.smartschool.api.entity.BusFeeRateStructure;
import com.smartschool.api.entity.StudentMonthlyFeeStructure;

import java.util.List;
import java.util.Map;

public interface BusMonthlyFeeService {
    // Fee Rate Structure Management
    BusFeeRateStructure createFeeRateStructure(Long stoppageId, Long busId, Long schoolId, Long academicYearId,
                                               double monthlyFeeAmount, int totalMonths, String academicYearStartMonth);
    BusFeeRateStructure updateFeeRateStructure(Long rateStructureId, double monthlyFeeAmount);
    BusFeeRateStructure getFeeRateStructure(Long stoppageId, Long busId, Long schoolId, Long academicYearId);
    List<BusFeeRateStructureDTO> getFeeRateStructuresByBus(Long busId, Long schoolId, Long academicYearId);
    void deleteFeeRateStructure(Long rateStructureId);

    // Student Monthly Fee Assignment
    StudentMonthlyFeeStructure assignStudentMonthlyFees(StudentBusAssignmentRequestDTO request);
    StudentMonthlyFeeStructure updateStudentFeeAssignment(Long assignmentId, List<String> selectedMonths, String paymentFrequency);
    StudentMonthlyFeeStructureDTO getStudentFeeAssignment(Long studentId, Long academicYearId);
    List<StudentMonthlyFeeStructureDTO> getStudentFeeAssignmentsByBus(Long busId, Long schoolId, Long academicYearId);
    List<StudentMonthlyFeeStructureDTO> getStudentFeeAssignmentsByStoppage(Long stoppageId, Long academicYearId);
    List<StudentMonthlyFeeStructureDTO> getStudentFeeHistory(Long studentId);
    List<TransportFeeLogDTO> getStudentPaymentHistory(Long studentId);
    void deleteStudentFeeAssignment(Long assignmentId);

    // Due Reports
    List<Map<String, Object>> getMonthlyFeesDueReport(Long schoolId, Long academicYearId);
    List<Map<String, Object>> getStudentMonthlyFeesBreakdown(Long studentId, Long academicYearId);
    double calculateTotalMonthlyFees(List<String> selectedMonths, String joiningMonth, double monthlyFeeAmount, int academicYearTotalMonths);
}

