package com.smartschool.api.dto;

import lombok.Data;

@Data
public class AcademicYearChangeRequest {

    // ✅ NEW: Kaun se school ka year change karna hai
    private Long schoolId;

    private String newYear;       // e.g. "2026-27"
    private Long adminUserId;     // Kaun change kar raha hai
    private String confirmDelete; // "CONFIRM" likhna zaroori hai
}
