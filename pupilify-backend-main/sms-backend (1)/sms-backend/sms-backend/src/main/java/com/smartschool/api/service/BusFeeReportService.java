package com.smartschool.api.service;

import com.smartschool.api.entity.StudentMonthlyFeeStructure;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface BusFeeReportService {
    /**
     * Generate Excel report of students with bus fee dues
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @return ByteArrayOutputStream containing Excel file
     */
    ByteArrayOutputStream generateBusFeesDueExcel(Long schoolId, Long academicYearId) throws IOException;

    /**
     * Send bus fees due report via email
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @param toEmail Email address to send to
     */
    void sendBusFeesReportEmail(Long schoolId, Long academicYearId, String toEmail) throws IOException;

    /**
     * Get list of students with dues
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @return List of StudentMonthlyFeeStructure with dues
     */
    List<StudentMonthlyFeeStructure> getStudentsWithDues(Long schoolId, Long academicYearId);
}

