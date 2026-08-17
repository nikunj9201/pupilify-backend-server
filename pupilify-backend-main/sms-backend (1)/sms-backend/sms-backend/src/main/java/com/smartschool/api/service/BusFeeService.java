package com.smartschool.api.service;

import com.smartschool.api.entity.BusFeePayment;
import com.smartschool.api.entity.BusFeeStructure;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BusFeeService {
    List<BusFeeStructure> createBulkFeeStructure(Long schoolId, Long busId, List<BusFeeStructure> feeStructures);
    BusFeeStructure updateFeeStructure(Long structureId, double amount);
    void deleteFeeStructure(Long structureId);
    BusFeePayment collectFee(String studentIdentifier, double amount, String paymentMode);
    List<Map<String, Object>> getDueReport(Long busId);
    List<BusFeeStructure> getStudentFeeStructure(Long studentId);
    byte[] generateBusFeeDueReportExcel(Long schoolId, Long academicYearId) throws IOException;
}