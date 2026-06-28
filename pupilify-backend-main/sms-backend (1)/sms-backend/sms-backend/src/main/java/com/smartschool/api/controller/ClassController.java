package com.smartschool.api.controller;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/classes")
@CrossOrigin("*")
public class ClassController {

    private static final Logger log = LoggerFactory.getLogger(ClassController.class);

    @Autowired private ClassRepository classRepository;
    @Autowired private SchoolRepository schoolRepository;
    @Autowired private SectionRepository sectionRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private MappingRepository mappingRepository;
    @Autowired private FeeStructureRepository feeStructureRepository;

    @PostMapping("/create/{schoolId}")
    public ResponseEntity<SchoolClass> createClass(@PathVariable Long schoolId, @RequestBody SchoolClass schoolClass) {
        log.info("Creating class for schoolId: {}", schoolId);
        School school = schoolRepository.findById(schoolId).orElseThrow();
        schoolClass.setSchool(school);
        schoolClass.setActive(true);
        SchoolClass saved = classRepository.save(schoolClass);
        log.info("Class '{}' created with ID: {}", saved.getClassName(), saved.getId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<SchoolClass>> getClassesBySchool(@PathVariable Long schoolId) {
        log.debug("Fetching active classes for schoolId: {}", schoolId);
        return ResponseEntity.ok(classRepository.findBySchoolIdAndIsActiveTrue(schoolId));
    }

    @DeleteMapping("/delete/{classId}")
    @Transactional
    public ResponseEntity<String> deleteClass(@PathVariable Long classId) {
        log.warn("Inactivation request for classId: {}", classId);

        if (studentRepository.existsBySchoolClassIdAndIsActiveTrue(classId)) {
            log.warn("Cannot inactivate classId:{} — active students exist", classId);
            return ResponseEntity.badRequest()
                    .body("Cannot inactivate! Active students are still present in this class.");
        }
        if (feeStructureRepository.existsBySchoolClassIdAndIsActiveTrue(classId)) {
            log.warn("Cannot inactivate classId:{} — active fee structure exists", classId);
            return ResponseEntity.badRequest()
                    .body("Action Denied! Archive the 'Fee Structure' for this class first.");
        }
        if (mappingRepository.existsBySchoolClassId(classId)) {
            log.warn("Cannot inactivate classId:{} — teacher mapping exists", classId);
            return ResponseEntity.badRequest()
                    .body("Cannot delete! A class teacher is still assigned.");
        }

        SchoolClass schoolClass = classRepository.findById(classId).orElseThrow();
        schoolClass.setActive(false);
        if (schoolClass.getSections() != null) {
            schoolClass.getSections().forEach(s -> s.setActive(false));
        }
        classRepository.save(schoolClass);
        log.info("ClassId:{} inactivated successfully.", classId);
        return ResponseEntity.ok("Class and its sections inactivated successfully.");
    }

    @PostMapping("/{classId}/sections/{schoolId}")
    public ResponseEntity<Section> addSection(
            @PathVariable Long classId, @PathVariable Long schoolId, @RequestBody Section section) {
        log.info("Adding section for classId:{} schoolId:{}", classId, schoolId);
        SchoolClass schoolClass = classRepository.findById(classId).orElseThrow();
        School school = schoolRepository.findById(schoolId).orElseThrow();
        section.setSchoolClass(schoolClass);
        section.setSchool(school);
        section.setActive(true);
        Section saved = sectionRepository.save(section);
        log.info("Section '{}' created with ID: {}", saved.getSectionName(), saved.getId());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/sections/delete/{sectionId}")
    @Transactional
    public ResponseEntity<String> deleteSection(@PathVariable Long sectionId) {
        log.warn("Inactivation request for sectionId: {}", sectionId);

        if (studentRepository.existsBySectionIdAndIsActiveTrue(sectionId)) {
            log.warn("Cannot inactivate sectionId:{} — active students exist", sectionId);
            return ResponseEntity.badRequest()
                    .body("Cannot inactivate! Active students are present in this section.");
        }
        if (feeStructureRepository.existsBySectionIdAndIsActiveTrue(sectionId)) {
            log.warn("Cannot inactivate sectionId:{} — fee structure exists", sectionId);
            return ResponseEntity.badRequest()
                    .body("Action Denied! Archive the Section Fee Structure first.");
        }

        Section section = sectionRepository.findById(sectionId).orElseThrow();
        section.setActive(false);
        sectionRepository.save(section);
        log.info("SectionId:{} inactivated.", sectionId);
        return ResponseEntity.ok("Section inactivated successfully.");
    }

    @GetMapping("/sections/school/{schoolId}")
    public ResponseEntity<List<Map<String, Object>>> getSectionsBySchool(@PathVariable Long schoolId) {
        log.debug("Fetching sections for schoolId: {}", schoolId);
        List<Section> sections = sectionRepository.findBySchoolIdAndIsActiveTrue(schoolId);
        return ResponseEntity.ok(sections.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("sectionName", s.getSectionName());
            map.put("classId", s.getSchoolClass().getId());
            map.put("className", s.getSchoolClass().getClassName());
            return map;
        }).collect(Collectors.toList()));
    }

    @GetMapping("/{classId}/sections")
    public ResponseEntity<List<Section>> getSectionsByClass(@PathVariable Long classId) {
        log.debug("Fetching sections for classId: {}", classId);
        return ResponseEntity.ok(sectionRepository.findBySchoolClassIdAndIsActiveTrue(classId));
    }

    @PutMapping("/update/{classId}")
    public ResponseEntity<SchoolClass> updateClass(@PathVariable Long classId, @RequestBody SchoolClass classDetails) {
        log.info("Updating classId: {}", classId);
        SchoolClass sc = classRepository.findById(classId).orElseThrow();
        sc.setClassName(classDetails.getClassName());
        return ResponseEntity.ok(classRepository.save(sc));
    }
}
