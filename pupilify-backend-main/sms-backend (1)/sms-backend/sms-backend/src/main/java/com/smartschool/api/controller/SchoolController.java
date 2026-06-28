package com.smartschool.api.controller;

import com.smartschool.api.entity.School;
import com.smartschool.api.service.SchoolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/superadmin/schools")
@CrossOrigin("*")
public class SchoolController {

    @Autowired private SchoolService schoolService;

    // ─────────────────────────────────────────────────────────
    // 1. EXISTNG: Add & Update (Untouched)
    // ─────────────────────────────────────────────────────────
    @PostMapping("/add")
    public ResponseEntity<?> addSchool(
            @RequestParam("schoolData") String schoolJson,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(required = false) Long currentYearId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            School school = mapper.readValue(schoolJson, School.class);
            School savedSchool = schoolService.createSchoolWithYear(school, currentYearId, logo);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSchool);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────
    // 2. NAYA: DIRECT ACADEMIC YEAR LINKING API ✅
    // ─────────────────────────────────────────────────────────
    /**
     * Kisi school ki academic year ID badalne ya save karne ke liye
     * PUT /api/superadmin/schools/update-academic-year/4?academicYearId=1
     */
    @PutMapping("/update-academic-year/{schoolId}")
    public ResponseEntity<?> updateSchoolAcademicYear(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId) {

        try {
            School updatedSchool = schoolService.updateSchoolYear(schoolId, academicYearId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "School's Academic Year updated successfully");
            response.put("schoolName", updatedSchool.getSchoolName());
            response.put("currentYear", updatedSchool.getCurrentYear().getCurrentYear());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSchool(
            @PathVariable Long id,
            @RequestParam("schoolData") String schoolJson,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            School school = mapper.readValue(schoolJson, School.class);
            return ResponseEntity.ok(schoolService.updateSchool(id, school, logo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all") public ResponseEntity<List<School>> getAll() { return ResponseEntity.ok(schoolService.getAllSchools()); }
    @GetMapping("/{id}") public ResponseEntity<School> get(@PathVariable Long id) { return ResponseEntity.ok(schoolService.getSchoolById(id)); }
    @DeleteMapping("/delete/{id}") public ResponseEntity<?> delete(@PathVariable Long id) { schoolService.deleteSchool(id); return ResponseEntity.ok("Deleted"); }

    // ─────────────────────────────────────────────────────────
    // 3. NEW: ASSIGN STATE & DISTRICT TO SCHOOL
    // ─────────────────────────────────────────────────────────
    @PutMapping("/{schoolId}/assign-state/{stateId}")
    public ResponseEntity<?> assignStateToSchool(
            @PathVariable Long schoolId,
            @PathVariable Long stateId) {
        try {
            School school = schoolService.assignStateToSchool(schoolId, stateId);
            return ResponseEntity.ok(Map.of(
                "message", "State assigned successfully",
                "schoolId", school.getId(),
                "stateId", school.getState().getId(),
                "stateName", school.getState().getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{schoolId}/assign-district/{districtId}")
    public ResponseEntity<?> assignDistrictToSchool(
            @PathVariable Long schoolId,
            @PathVariable Long districtId) {
        try {
            School school = schoolService.assignDistrictToSchool(schoolId, districtId);
            return ResponseEntity.ok(Map.of(
                "message", "District assigned successfully",
                "schoolId", school.getId(),
                "districtId", school.getDistrict().getId(),
                "districtName", school.getDistrict().getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────
    // 4. GET SCHOOLS BY STATE & DISTRICT (for state/district managers)
    // ─────────────────────────────────────────────────────────
    @GetMapping("/by-state/{stateId}")
    public ResponseEntity<List<School>> getByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(schoolService.getSchoolsByState(stateId));
    }

    @GetMapping("/by-district/{districtId}")
    public ResponseEntity<List<School>> getByDistrict(@PathVariable Long districtId) {
        return ResponseEntity.ok(schoolService.getSchoolsByDistrict(districtId));
    }
}