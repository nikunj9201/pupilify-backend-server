package com.smartschool.api.controller;

import com.smartschool.api.entity.StudentMonthlyFeeStructure;
import com.smartschool.api.service.BusFeeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/bus-fees-report")
@CrossOrigin("*")
public class BusFeeReportController {

    @Autowired
    private BusFeeReportService busFeeReportService;

    /**
     * Get all students with pending bus fees for a school in an academic year
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @return List of students with dues
     */
    @GetMapping("/students-with-dues/{schoolId}")
    public ResponseEntity<List<StudentMonthlyFeeStructure>> getStudentsWithDues(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId) {
        List<StudentMonthlyFeeStructure> dues = busFeeReportService.getStudentsWithDues(schoolId, academicYearId);
        return ResponseEntity.ok(dues);
    }

    /**
     * Download Excel report of bus fees dues
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @return Excel file for download
     */
    @GetMapping("/download-excel/{schoolId}")
    public ResponseEntity<byte[]> downloadExcelReport(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId) throws IOException {

        ByteArrayOutputStream excelFile = busFeeReportService.generateBusFeesDueExcel(schoolId, academicYearId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename("BusFeesReport_" + System.currentTimeMillis() + ".xlsx")
                .build());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile.toByteArray());
    }

    /**
     * Send bus fees due report via email to school
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @param toEmail Email address to send to
     * @return Success response
     */
    @PostMapping("/send-email/{schoolId}")
    public ResponseEntity<String> sendBusFeeReportEmail(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam String toEmail) throws IOException {

        busFeeReportService.sendBusFeesReportEmail(schoolId, academicYearId, toEmail);

        return ResponseEntity.ok("Bus fees due report sent successfully to: " + toEmail);
    }

    /**
     * Send bus fees due report to school's registered email
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @return Success response
     */
    @PostMapping("/send-to-school/{schoolId}")
    public ResponseEntity<String> sendBusFeeReportToSchoolEmail(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId) throws IOException {

        // Note: This will use the school's mailId from database
        // Implementation should fetch school and use school.getMailId()
        // For now, just call the service with a placeholder - actual integration
        // happens in AcademicYearServiceImpl

        return ResponseEntity.ok("Please use the sendBusFeesReportEmail endpoint with the school's mailId");
    }

    /**
     * Get count of students with pending bus fees
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @return Count of students with dues
     */
    @GetMapping("/count-dues/{schoolId}")
    public ResponseEntity<Long> countStudentsWithDues(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId) {
        List<StudentMonthlyFeeStructure> dues = busFeeReportService.getStudentsWithDues(schoolId, academicYearId);
        return ResponseEntity.ok((long) dues.size());
    }

    /**
     * Get total due amount for all students
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @return Total due amount in rupees
     */
    @GetMapping("/total-dues/{schoolId}")
    public ResponseEntity<Double> getTotalDuesAmount(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId) {
        List<StudentMonthlyFeeStructure> dues = busFeeReportService.getStudentsWithDues(schoolId, academicYearId);
        double totalDue = dues.stream()
                .mapToDouble(StudentMonthlyFeeStructure::getTotalFeeAmount)
                .sum();
        return ResponseEntity.ok(totalDue);
    }
}

