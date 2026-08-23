package com.smartschool.api.dto;

import com.smartschool.api.entity.TransportFeeLog;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TransportFeeLogDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long academicYearId;
    private String monthYear;
    private double amountDue;
    private double amountPaid;
    private TransportFeeLog.FeeStatus status;
    private LocalDate paymentDate;
    private String paymentMode;

    public static TransportFeeLogDTO fromEntity(TransportFeeLog log) {
        TransportFeeLogDTO dto = new TransportFeeLogDTO();
        dto.setId(log.getId());
        dto.setStudentId(log.getStudent().getId());
        dto.setStudentName(log.getStudent().getName());
        dto.setAcademicYearId(log.getAcademicYear().getId());
        dto.setMonthYear(log.getMonthYear());
        dto.setAmountDue(log.getAmountDue());
        dto.setAmountPaid(log.getAmountPaid());
        dto.setStatus(log.getStatus());
        dto.setPaymentDate(log.getPaymentDate());
        dto.setPaymentMode(log.getPaymentMode());
        return dto;
    }
}