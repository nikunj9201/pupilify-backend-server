package com.smartschool.api.service;

import com.smartschool.api.dto.AcademicYearChangeRequest;
import com.smartschool.api.dto.AcademicYearStatusResponse;

public interface AcademicYearService {

    // ✅ School-specific current year
    String getCurrentAcademicYear(Long schoolId);

    // ✅ School-specific status
    AcademicYearStatusResponse getStatus(Long schoolId);

    // AcademicYearService.java mein:
    AcademicYearStatusResponse initializeFirstYear(Long schoolId, String currentYear, Long adminUserId);

    // ✅ Sirf ek school ka year change
    AcademicYearStatusResponse changeAcademicYear(
            Long schoolId, AcademicYearChangeRequest request);
}
