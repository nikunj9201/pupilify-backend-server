package com.smartschool.api.controller;

import com.smartschool.api.entity.ClassTeacherMapping;
import com.smartschool.api.service.ClassTeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
// 🚩 ADDED: Dono paths handle karne ke liye (Nginx compatibility fix)
@RequestMapping({"/api/admin/mappings", "/admin/mappings"})
@CrossOrigin("*")
public class ClassTeacherController {

    private static final Logger log = LoggerFactory.getLogger(ClassTeacherController.class);

    @Autowired private ClassTeacherService classTeacherService;

    // --- 1. NEW FEATURE: GET MAPPINGS BY TEACHER ID (For Dashboard) ---
    // Teacher jab login karega, toh ye endpoint uski classes load karega
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassTeacherMapping>> getByTeacher(@PathVariable Long teacherId) {
        log.info("Fetching mappings for teacherId: {}", teacherId);
        try {
            List<ClassTeacherMapping> mappings = classTeacherService.getMappingsByTeacher(teacherId);
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            log.error("Error fetching teacher mappings: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- 2. EXISTING: ASSIGN (Purana code bilkul waisa hi hai) ---
    @PostMapping("/assign/{classId}/{sectionId}/{teacherId}/{schoolId}")
    public ResponseEntity<?> assign(@PathVariable Long classId, @PathVariable Long sectionId,
                                    @PathVariable Long teacherId, @PathVariable Long schoolId,
                                    @RequestParam Long academicYearId) {
        log.info("Assigning teacherId:{} to classId:{} sectionId:{} for year ID:{}", teacherId, classId, sectionId, academicYearId);
        try {
            ClassTeacherMapping result = classTeacherService.assignClassTeacher(classId, sectionId, teacherId, schoolId, academicYearId);
            log.info("Teacher mapping created. ID: {}", result.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.warn("Teacher mapping FAILED: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- 3. EXISTING: GET ALL BY SCHOOL ---
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<ClassTeacherMapping>> getAll(@PathVariable Long schoolId) {
        log.debug("Fetching active mappings for schoolId: {}", schoolId);
        return ResponseEntity.ok(classTeacherService.getMappingsBySchool(schoolId));
    }

    // --- 4. EXISTING: DELETE ---
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.warn("Soft-deleting mapping ID: {}", id);
        classTeacherService.deleteMapping(id);
        log.info("Mapping ID:{} inactivated.", id);
        return ResponseEntity.ok("Mapping deleted.");
    }
}