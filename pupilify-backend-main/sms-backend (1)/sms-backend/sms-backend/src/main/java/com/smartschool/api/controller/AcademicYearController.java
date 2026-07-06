package com.smartschool.api.controller;

import com.smartschool.api.dto.AcademicYearChangeRequest;
import com.smartschool.api.dto.AcademicYearStatusResponse;
import com.smartschool.api.entity.AcademicYearConfig;
import com.smartschool.api.repository.AcademicYearConfigRepository;
import com.smartschool.api.service.AcademicYearService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/academic-year")
@CrossOrigin("*")
public class AcademicYearController {

    @Autowired
    private AcademicYearService academicYearService;

    @Autowired
    private AcademicYearConfigRepository academicYearConfigRepository;

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<AcademicYearConfig>> getAcademicYearsBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(academicYearConfigRepository.findBySchoolId(schoolId));
    }

    /**
     * School ka current year fetch karo
     * GET /api/superadmin/academic-year/current/{schoolId}
     */
    @GetMapping("/current/{schoolId}")
    public ResponseEntity<Map<String, String>> getCurrentYear(
            @PathVariable Long schoolId) {
        String year = academicYearService.getCurrentAcademicYear(schoolId);
        return ResponseEntity.ok(Map.of(
                "schoolId", schoolId.toString(),
                "currentYear", year,
                "message", "Fetched successfully."));
    }

    /**
     * School ka status check karo
     * GET /api/superadmin/academic-year/status/{schoolId}
     */
    @GetMapping("/status/{schoolId}")
    public ResponseEntity<AcademicYearStatusResponse> getStatus(
            @PathVariable Long schoolId) {
        return ResponseEntity.ok(
                academicYearService.getStatus(schoolId));
    }

    /**
     * School ka academic year change karo
     * POST /api/superadmin/academic-year/change/{schoolId}
     */
    @PostMapping("/change/{schoolId}")
    public ResponseEntity<?> changeAcademicYear(
            @PathVariable Long schoolId,
            @RequestBody AcademicYearChangeRequest request) {

        log.warn("Year change API called. School ID: {} | New Year: {}",
                schoolId, request.getNewYear());
        try {
            AcademicYearStatusResponse response =
                    academicYearService.changeAcademicYear(schoolId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Year change FAILED: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Academic year change failed",
                            "message", e.getMessage()));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 🚩 NAYA ENDPOINT: PEHLI BAAR YEAR CONFIG BANANE KE LIYE ✅
    // ──────────────────────────────────────────────────────────────
    /**
     * School ka pehla academic year initialize karo
     * POST /api/superadmin/academic-year/initialize/{schoolId}
     * Params: currentYear="2025-26", adminUserId=1
     */
    @PostMapping("/initialize/{schoolId}")
    public ResponseEntity<?> initializeAcademicYear(
            @PathVariable Long schoolId,
            @RequestParam String currentYear,
            @RequestParam Long adminUserId) {

        log.info("Initializing first academic year for School ID: {} | Year: {}", schoolId, currentYear);
        try {
            AcademicYearStatusResponse response =
                    academicYearService.initializeFirstYear(schoolId, currentYear, adminUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Initialization FAILED: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Initialization failed",
                            "message", e.getMessage()));
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 🚩 NOTE: Linking API SchoolController mein pehle se hai,
    // par convenience ke liye yahan logic controller ke through link hai.
    // ──────────────────────────────────────────────────────────────
}