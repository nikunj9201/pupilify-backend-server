package com.smartschool.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentMonthlyFeeStructureDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long stoppageId;
    private String stopName;
    private Long busId;
    private Long schoolId;
    private Long academicYearId;
    private double monthlyFeeAmount;
    private List<String> selectedMonths;
    private String joiningMonth;
    private int joiningYear;
    private String paymentFrequency; // MONTHLY or YEARLY
    private double totalFeeAmount;
    private int totalMonthsSelected;
    private String assignmentDate;
    private boolean active;
}

