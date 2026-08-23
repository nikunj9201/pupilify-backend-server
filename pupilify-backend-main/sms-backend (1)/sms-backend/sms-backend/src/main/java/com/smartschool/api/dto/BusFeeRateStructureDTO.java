package com.smartschool.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusFeeRateStructureDTO {
    private Long id;
    private Long stoppageId;
    private String stopName;
    private Long busId;
    private Long schoolId;
    private Long academicYearId;
    private double monthlyFeeAmount;
    private int totalMonths;
    private String academicYearStartMonth;
    private boolean active;
}

