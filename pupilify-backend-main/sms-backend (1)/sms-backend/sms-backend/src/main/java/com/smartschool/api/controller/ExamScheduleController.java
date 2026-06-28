package com.smartschool.api.controller;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/exams")
@CrossOrigin("*")
public class ExamScheduleController {

    @Autowired private ExamScheduleRepository examRepo;
    @Autowired private SubjectRepository subjectRepo;
    @Autowired private AcademicYearConfigRepository yearRepo;
    @Autowired private SchoolRepository schoolRepo;
    @Autowired private ClassRepository classRepo;
    @Autowired private SectionRepository sectionRepo;

    // 🚩 1. Generate Single Exam Slot (Existing Feature)
    @PostMapping("/generate/{schoolId}/{classId}")
    public ResponseEntity<?> generateSchedule(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam Long subjectId,
            @RequestParam Long academicYearId,
            @RequestBody ExamSchedule schedule) {

        School school = schoolRepo.findById(schoolId).orElseThrow();
        SchoolClass sClass = classRepo.findById(classId).orElseThrow();
        AcademicYearConfig year = yearRepo.findById(academicYearId).orElseThrow();
        Subject subject = subjectRepo.findById(subjectId).orElseThrow();

        if (!subject.getSchoolClass().getId().equals(classId)) {
            return ResponseEntity.badRequest().body("Subject class mismatch!");
        }

        schedule.setSchool(school);
        schedule.setSchoolClass(sClass);
        schedule.setAcademicYear(year);
        schedule.setSubject(subject);

        if (sectionId != null) {
            schedule.setSection(sectionRepo.findById(sectionId).orElseThrow());
        } else {
            schedule.setSection(null);
        }

        schedule.setActive(true);
        return ResponseEntity.ok(examRepo.save(schedule));
    }

    // 🚩 NEW FEATURE: Bulk Generate Exam Timetable (Class & Section Wise)
    @PostMapping("/bulk-generate/{schoolId}/{classId}")
    public ResponseEntity<?> generateBulkSchedule(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam Long academicYearId,
            @RequestBody List<ExamSchedule> schedules) {

        try {
            School school = schoolRepo.findById(schoolId).orElseThrow(() -> new RuntimeException("School not found"));
            SchoolClass sClass = classRepo.findById(classId).orElseThrow(() -> new RuntimeException("Class not found"));
            AcademicYearConfig year = yearRepo.findById(academicYearId).orElseThrow(() -> new RuntimeException("Academic Year not found"));

            // Section logic: Agar sectionId hai toh use find karo, warna null
            Section section = (sectionId != null) ? sectionRepo.findById(sectionId).orElse(null) : null;

            List<ExamSchedule> finalSchedules = new ArrayList<>();

            for (ExamSchedule schedule : schedules) {
                if (schedule.getSubject() == null || schedule.getSubject().getId() == null) {
                    return ResponseEntity.badRequest().body("Galti: Subject ID missing hai");
                }

                Subject subject = subjectRepo.findById(schedule.getSubject().getId()).orElseThrow();

                // Validation: Subject isi class ka hona chahiye
                if (!subject.getSchoolClass().getId().equals(classId)) {
                    return ResponseEntity.badRequest().body("Galti: Subject '" + subject.getSubjectName() + "' is class ka nahi hai!");
                }

                schedule.setSchool(school);
                schedule.setSchoolClass(sClass);
                schedule.setSection(section);
                schedule.setAcademicYear(year);
                schedule.setSubject(subject);
                schedule.setActive(true);

                finalSchedules.add(schedule);
            }

            List<ExamSchedule> saved = examRepo.saveAll(finalSchedules);
            String target = (section != null) ? "Section " + section.getSectionName() : "Poori Class";
            return ResponseEntity.ok(saved.size() + " Subjects ka timetable " + target + " ke liye save ho gaya.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // 🚩 2. Dashboard View (Student/Principal dono ke liye)
    @GetMapping("/view-timetable/{schoolId}")
    public ResponseEntity<List<ExamSchedule>> getTimetable(
            @PathVariable Long schoolId,
            @RequestParam Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam Long academicYearId,
            @RequestParam(required = false) String examName) {

        List<ExamSchedule> timetable = examRepo.findFilteredSchedule(schoolId, classId, sectionId, academicYearId, examName);
        return ResponseEntity.ok(timetable);
    }

    // 🚩 3. School mein kaun-kaun si exams scheduled hain (Dropdown ke liye)
    @GetMapping("/exam-list/{schoolId}")
    public ResponseEntity<List<String>> getExamNames(@PathVariable Long schoolId, @RequestParam Long academicYearId) {
        return ResponseEntity.ok(examRepo.findUniqueExamNames(schoolId, academicYearId));
    }

    // 🚩 4. Delete/Deactivate Schedule
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long id) {
        ExamSchedule schedule = examRepo.findById(id).orElseThrow();
        schedule.setActive(false);
        examRepo.save(schedule);
        return ResponseEntity.ok("Schedule deleted.");
    }
}