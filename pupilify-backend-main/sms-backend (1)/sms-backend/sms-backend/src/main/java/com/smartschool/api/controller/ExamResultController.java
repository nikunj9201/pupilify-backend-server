package com.smartschool.api.controller;

import com.smartschool.api.dto.ClassResultResponse;
import com.smartschool.api.dto.ExamResultRequest;
import com.smartschool.api.dto.ResultCardResponse;
import com.smartschool.api.entity.ExamResult;
import com.smartschool.api.entity.Student;
import com.smartschool.api.repository.StudentRepository;
import com.smartschool.api.service.ExamResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "*")
public class ExamResultController {

    @Autowired
    private ExamResultService resultService;

    @Autowired
    private StudentRepository studentRepository;


    // ─────────────────────────────────────────────────────────
    // 1. BULK UPLOAD (Original endpoint)
    // ─────────────────────────────────────────────────────────
    @PostMapping("/admin/upload-bulk")
    public ResponseEntity<?> uploadBulkResults(
            @RequestParam Long schoolId,
            @RequestParam Long examScheduleId,
            @RequestParam Long academicYearId,
            @RequestBody List<ExamResultRequest> requests) {
        try {
            // 🚩 Input validation
            if (requests == null || requests.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Request list is empty",
                    "errorCode", "EMPTY_REQUEST"
                ));
            }

            log.info("📤 Received bulk upload request: schoolId={}, examScheduleId={}, academicYearId={}, count={}",
                    schoolId, examScheduleId, academicYearId, requests.size());

            // Log first request for debugging
            if (!requests.isEmpty()) {
                ExamResultRequest first = requests.get(0);
                log.info("   First request: studentId={}, teacherId={}, isAbsent={}, theory={}, practical={}",
                        first.getStudentId(), first.getTeacherId(), first.isAbsent(),
                        first.getMarksObtainedTheory(), first.getMarksObtainedPractical());
            }

            // ✅ Call service (stores in DB but DON'T return the entities)
            List<ExamResult> saved = resultService.uploadBulkResults(schoolId, examScheduleId, academicYearId, requests);

            log.info("✅ Bulk upload successful: {} records saved", saved.size());

            // Return only summary (NO entity objects)
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Upload complete",
                "savedCount", saved.size(),
                "detail", saved.size() + " students ke marks save ho gaye"
            ));
        } catch (Exception e) {
            log.error("❌ Bulk upload failed: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(Map.of(
                "success", false,
                "message", "Bulk upload failed",
                "error", e.getMessage(),
                "errorCode", "UPLOAD_FAILED"
            ));
        }
    }

    // ─────────────────────────────────────────────────────────
    // 2. GET BY ENROLLMENT ID (Result Card)
    // ─────────────────────────────────────────────────────────
    @GetMapping("/admin/by-enrollment/{enrollmentId}/{schoolId}")
    public ResponseEntity<ApiResponse<ResultCardResponse>> getResultByEnrollmentId(
            @PathVariable String enrollmentId,
            @PathVariable Long schoolId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam String examName,
            @RequestParam Long academicYearId) {

        try {
            // Service ab ResultCardResponse bhejegi jisme Enrollment ID aur Roll Number dono hain
            ResultCardResponse card = resultService.getStudentResultByEnrollment(
                    enrollmentId, schoolId, classId, sectionId, examName, academicYearId);

            return ResponseEntity.ok(ApiResponse.success("Success", card));
        } catch (Exception e) {
            log.error("Enrollment ID result error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}/{schoolId}")
    public ResponseEntity<ApiResponse<ResultCardResponse>> getStudentResult(
            @PathVariable Long studentId,
            @PathVariable Long schoolId,
            @RequestParam String examName,
            @RequestParam Long academicYearId) {

        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

            ResultCardResponse card = resultService.getStudentResultByEnrollment(
                    student.getEnrollmentId(), schoolId, student.getSchoolClass().getId(), student.getSection().getId(), examName, academicYearId);

            return ResponseEntity.ok(ApiResponse.success("Success", card));
        } catch (Exception e) {
            log.error("Student result error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────
    // 3. CLASS RESULT SHEET
    // ─────────────────────────────────────────────────────────
    @GetMapping("/admin/class-sheet/{schoolId}/{classId}")
    public ResponseEntity<ApiResponse<ClassResultResponse>> getClassResultSheet(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam String examName,
            @RequestParam Long academicYearId) {
        try {
            // Response mein ab har student ke liye dono IDs dikhengi
            ClassResultResponse sheet = resultService.getClassResultSheet(schoolId, classId, sectionId, examName, academicYearId);
            return ResponseEntity.ok(ApiResponse.success("Success", sheet));
        } catch (Exception e) {
            log.error("Class result sheet error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────
    // 4. EXCEL EXPORT & OTHERS (No changes needed)
    // ─────────────────────────────────────────────────────────

    @GetMapping("/admin/export-excel/{schoolId}/{classId}")
    public ResponseEntity<byte[]> exportResultExcel(@PathVariable Long schoolId, @PathVariable Long classId, @RequestParam(required = false) Long sectionId, @RequestParam String examName, @RequestParam Long academicYearId) {
        try {
            byte[] excelBytes = resultService.generateClassResultExcel(schoolId, classId, sectionId, examName, academicYearId);
            String filename = "Result_" + examName.replaceAll("[^a-zA-Z0-9]", "_") + ".xlsx";
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(excelBytes);
        } catch (Exception e) { return ResponseEntity.internalServerError().build(); }
    }

    @GetMapping("/admin/exam-names")
    public ResponseEntity<ApiResponse<List<String>>> getExamNames(@RequestParam Long schoolId, @RequestParam Long academicYearId) {
        try {
            List<String> names = resultService.getUniqueExamNames(schoolId, academicYearId);
            return ResponseEntity.ok(ApiResponse.success("Exams found", names));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage())); }
    }

    // Wrapper Record
    record ApiResponse<T>(boolean success, String message, T data) {
        static <T> ApiResponse<T> success(String msg, T data) { return new ApiResponse<>(true, msg, data); }
        static <T> ApiResponse<T> error(String msg) { return new ApiResponse<>(false, msg, null); }
    }
}