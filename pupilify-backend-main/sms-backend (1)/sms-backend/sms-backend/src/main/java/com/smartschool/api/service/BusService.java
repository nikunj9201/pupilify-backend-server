package com.smartschool.api.service;

import com.smartschool.api.dto.BusAssignmentDTO;
import com.smartschool.api.dto.TransportFeeLogDTO;
import com.smartschool.api.entity.Bus;
import com.smartschool.api.entity.StudentBusAssignment;
import com.smartschool.api.entity.TransportFeeLog;

import java.util.List;
import java.util.Map;

public interface BusService {
    Bus addBus(Long schoolId, String registrationNo, int capacity);
    List<Bus> getBusesBySchool(Long schoolId);
    StudentBusAssignment assignStudentToBus(Long studentId, Long stoppageId, Long academicYearId);
    void generateMonthlyFees(Long schoolId, Long academicYearId, List<String> months);
    TransportFeeLogDTO collectBusFee(Long studentId, Long academicYearId, double amount, String paymentMode);
    Map<String, Object> collectYearlyBusFee(Long studentId, Long academicYearId, double amount, String paymentMode);
    List<Map<String, Object>> getBusFeeDueReport(Long schoolId, Long academicYearId);
    StudentBusAssignment getStudentBusAssignment(Long studentId, Long academicYearId);
    List<BusAssignmentDTO> getAssignmentsByRoute(Long routeId);
    void deleteBus(Long schoolId, Long busId);
    List<TransportFeeLogDTO> getFeeHistory(Long studentId, Long academicYearId);
    void unassignStudent(Long assignmentId);
}