package com.smartschool.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentBusAssignmentRequestDTO {
    private Long studentId;
    private Long stoppageId;
    private Long busId;
    private Long schoolId;
    private Long academicYearId;
    private List<String> selectedMonths; // e.g., ["July", "August", "September", ...]
    private String joiningMonth; // Month when student joins (for late joining)
    private String paymentFrequency; // MONTHLY or YEARLY
}

