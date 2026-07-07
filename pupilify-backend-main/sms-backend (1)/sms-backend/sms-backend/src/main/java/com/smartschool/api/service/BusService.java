package com.smartschool.api.service;

import com.smartschool.api.entity.BusFeePayment;
import com.smartschool.api.entity.StudentBusAssignment;

import java.util.List;
import java.util.Map;

public interface BusService {
    StudentBusAssignment assignStudentToBus(Long studentId, Long stoppageId, Long academicYearId);
    BusFeePayment collectBusFee(Long studentId, Long academicYearId, double amount, String paymentMode);
    List<Map<String, Object>> getBusFeeDueReport(Long schoolId, Long academicYearId);
}