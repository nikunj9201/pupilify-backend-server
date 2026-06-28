package com.smartschool.api.controller;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
// Support both /api/admin/subjects and /admin/subjects so frontend and alternate proxies both work
@RequestMapping({"/api/admin/subjects", "/admin/subjects"})
@CrossOrigin("*")
public class SubjectController {

    @Autowired private SubjectRepository subjectRepository;
    @Autowired private AcademicYearConfigRepository yearRepo;
    @Autowired private SectionRepository sectionRepository;
    @Autowired private ClassRepository classRepository;
    @Autowired private SchoolRepository schoolRepository;

    @PostMapping("/create/{classId}/{sectionId}/{schoolId}")
    public ResponseEntity<Subject> createSubject(
            @PathVariable Long classId, @PathVariable Long sectionId, @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestBody Subject subject) { // 🚩 Subject body mein marks aayenge

        // 1. Fetch Academic Year Entity [cite: 709, 710]
        AcademicYearConfig year = yearRepo.findById(academicYearId)
                .orElseThrow(() -> new RuntimeException("Academic Year not found"));

        // 2. Set Relations [cite: 711, 712, 713]
        subject.setAcademicYear(year);
        subject.setSchoolClass(classRepository.findById(classId).orElseThrow());
        subject.setSchool(schoolRepository.findById(schoolId).orElseThrow());

        // 3. Section Handling [cite: 714, 715]
        if (sectionId != null && sectionId > 0) {
            subject.setSection(sectionRepository.findById(sectionId).orElseThrow());
        } else {
            subject.setSection(null);
        }

        // Theory aur Practical marks automatically save honge
        subject.setActive(true);
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    // Helper method to convert Subject entity to SubjectDTO
    private com.smartschool.api.dto.SubjectDTO convertToDTO(Subject s) {
        com.smartschool.api.dto.SubjectDTO d = new com.smartschool.api.dto.SubjectDTO();
        d.setId(s.getId());
        d.setSubjectName(s.getSubjectName());
        d.setSubjectCode(s.getSubjectCode());
        d.setTotalTheoryMarks(s.getTotalTheoryMarks());
        d.setPassingTheoryMarks(s.getPassingTheoryMarks());
        d.setHasPractical(s.isHasPractical());
        d.setTotalPracticalMarks(s.getTotalPracticalMarks());
        d.setPassingPracticalMarks(s.getPassingPracticalMarks());
        d.setActive(s.isActive());
        d.setAcademicYearId(s.getAcademicYear() != null ? s.getAcademicYear().getId() : null);
        d.setSchoolClassId(s.getSchoolClass() != null ? s.getSchoolClass().getId() : null);
        d.setClassName(s.getSchoolClass() != null ? s.getSchoolClass().getClassName() : null);
        d.setSectionId(s.getSection() != null ? s.getSection().getId() : null);
        d.setSectionName(s.getSection() != null ? s.getSection().getSectionName() : null);
        d.setSchoolId(s.getSchool() != null ? s.getSchool().getId() : null);
        return d;
    }

    @GetMapping("/school/{schoolId}/class/{classId}")
    public ResponseEntity<List<com.smartschool.api.dto.SubjectDTO>> getSubjectsByClass(
            @PathVariable Long schoolId, @PathVariable Long classId, @RequestParam Long academicYearId) {
        List<Subject> subjects = subjectRepository.findBySchoolIdAndSchoolClassIdAndIsActiveTrueAndAcademicYearId(schoolId, classId, academicYearId);
        List<com.smartschool.api.dto.SubjectDTO> dtos = subjects.stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/school/{schoolId}/class/{classId}/section/{sectionId}")
    public ResponseEntity<List<com.smartschool.api.dto.SubjectDTO>> getSubjectsByClassAndSection(
            @PathVariable Long schoolId, @PathVariable Long classId, @PathVariable Long sectionId, @RequestParam Long academicYearId) {

        Long finalSecId = (sectionId != null && sectionId > 0) ? sectionId : null;
        List<Subject> subjects = subjectRepository.findActiveSubjects(schoolId, classId, finalSecId, academicYearId);
        List<com.smartschool.api.dto.SubjectDTO> dtos = subjects.stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/all/{schoolId}")
    public ResponseEntity<List<com.smartschool.api.dto.SubjectDTO>> getAllSchoolSubjects(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId) {

        // Sirf schoolId aur academicYearId ke basis par saare active subjects fetch honge
        List<Subject> subjects = subjectRepository.findBySchoolIdAndAcademicYearIdAndIsActiveTrue(schoolId, academicYearId);

        // Map to DTO to avoid serializing lazy relationships directly
        List<com.smartschool.api.dto.SubjectDTO> dtos = subjects.stream().map(this::convertToDTO).toList();

        return ResponseEntity.ok(dtos);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        Subject subject = subjectRepository.findById(id).orElseThrow();
        subject.setActive(false);
        subjectRepository.save(subject);
        return ResponseEntity.ok("Subject inactivated!");
    }
}