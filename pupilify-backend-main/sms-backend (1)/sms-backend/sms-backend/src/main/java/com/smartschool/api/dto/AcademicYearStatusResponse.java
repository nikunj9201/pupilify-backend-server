package com.smartschool.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicYearStatusResponse {
    private String currentYear;
    private String previousYear;
    private boolean emailSent;
    private int emailsSentCount;
    private String status;
    private String message;
}