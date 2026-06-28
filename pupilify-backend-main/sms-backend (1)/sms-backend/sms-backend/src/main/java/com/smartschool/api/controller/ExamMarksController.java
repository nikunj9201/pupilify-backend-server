package com.smartschool.api.controller;

import com.smartschool.api.dto.ExamResultRequest;
import com.smartschool.api.entity.ExamResult;
import com.smartschool.api.service.ExamResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/exams/marks")
@CrossOrigin(origins = "*")
public class ExamMarksController {

    @Autowired
    private ExamResultService resultService;

    // ─────────────────────────────────────────────────────────
    // BULK UPLOAD MARKS FOR EXAM
    // ─────────────────────────────────────────────────────────
    @PostMapping("/bulk/{classId}/{sectionId}")
    public ResponseEntity<?> uploadMarksBulk(
            @PathVariable Long classId,
            @PathVariable Long sectionId,
            @RequestParam Long academicYearId,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long examScheduleId,
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

            // Validate required parameters
            if (schoolId == null || examScheduleId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "schoolId and examScheduleId are required",
                    "errorCode", "MISSING_PARAMS"
                ));
            }

            log.info("📤 Received marks bulk upload: classId={}, sectionId={}, schoolId={}, examScheduleId={}, academicYearId={}, count={}",
                    classId, sectionId, schoolId, examScheduleId, academicYearId, requests.size());

            // Log first request for debugging
            if (!requests.isEmpty()) {
                ExamResultRequest first = requests.getFirst();
                log.info("   First request: studentId={}, teacherId={}, isAbsent={}, theory={}, practical={}",
                        first.getStudentId(), first.getTeacherId(), first.isAbsent(),
                        first.getMarksObtainedTheory(), first.getMarksObtainedPractical());
            }

            // ✅ Call service
            List<ExamResult> saved = resultService.uploadBulkResults(schoolId, examScheduleId, academicYearId, requests);

            log.info("✅ Marks bulk upload successful: {} records saved", saved.size());

            // Return only summary
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Marks upload complete",
                "savedCount", saved.size(),
                "detail", saved.size() + " students ke marks save ho gaye"
            ));
        } catch (Exception e) {
            log.error("❌ Marks bulk upload failed: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(Map.of(
                "success", false,
                "message", "Marks upload failed",
                "error", e.getMessage(),
                "errorCode", "UPLOAD_FAILED"
            ));
        }
    }
}

