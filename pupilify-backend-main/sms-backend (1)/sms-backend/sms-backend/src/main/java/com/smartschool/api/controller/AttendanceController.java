package com.smartschool.api.controller;

import com.smartschool.api.entity.Attendance;
import com.smartschool.api.entity.Student;
import com.smartschool.api.entity.ClassTeacherMapping;
import com.smartschool.api.service.AttendanceService;
import com.smartschool.api.service.StudentService;
import com.smartschool.api.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/attendance")
@CrossOrigin("*")
public class AttendanceController {

    private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired private AttendanceService attendanceService;
    @Autowired private StudentService studentService;
    @Autowired private TeacherService teacherService;

    // ─────────────────────────────────────────────────────────────────────
    // API 1: Teacher ki active classes
    // ─────────────────────────────────────────────────────────────────────
    @GetMapping("/my-active-mappings/{teacherId}")
    public ResponseEntity<List<ClassTeacherMapping>> getMyActiveMappings(
            @PathVariable Long teacherId) {
        return ResponseEntity.ok(teacherService.getActiveMappingsForTeacher(teacherId));
    }

    // ─────────────────────────────────────────────────────────────────────
    // API 2: Teacher ke liye students list
    // ─────────────────────────────────────────────────────────────────────
    @GetMapping("/students-list-for-teacher/{schoolId}/{teacherId}")
    public ResponseEntity<List<Student>> getStudentsForTeacher(
            @PathVariable Long schoolId,
            @PathVariable Long teacherId) {

        List<ClassTeacherMapping> mappings =
                teacherService.getActiveMappingsForTeacher(teacherId);

        if (mappings.isEmpty()) {
            throw new RuntimeException("Aapke paas koi active class assigned nahi hai!");
        }

        ClassTeacherMapping activeMap = mappings.get(0);
        Long classId = activeMap.getSchoolClass().getId();
        Long sectionId = (activeMap.getSection() != null)
                ? activeMap.getSection().getId() : null;
        Long academicYearId = (activeMap.getAcademicYear() != null)
                ? activeMap.getAcademicYear().getId() : null;

        if (academicYearId == null) {
            throw new RuntimeException(
                    "Class teacher mapping mein academic year set nahi hai!");
        }

        return ResponseEntity.ok(studentService.getStudentsByClassAndSection(
                schoolId, classId, sectionId, academicYearId));
    }

    // ─────────────────────────────────────────────────────────────────────
    // API 3: Bulk Attendance Mark
    // ─────────────────────────────────────────────────────────────────────
    @PostMapping("/mark-bulk/{schoolId}/{classId}/{teacherId}")
    public ResponseEntity<String> markBulkAttendance(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @PathVariable Long teacherId,
            @RequestParam(required = false, defaultValue = "0") Long sectionId,
            @RequestBody List<Attendance> attendanceList) {

        return ResponseEntity.ok(attendanceService.markBulkAttendance(
                schoolId, classId, sectionId, teacherId, attendanceList));
    }

    // ─────────────────────────────────────────────────────────────────────
    // API 4: Report dekhna (date wise)
    // ─────────────────────────────────────────────────────────────────────
    @GetMapping("/view-report/{schoolId}/{classId}")
    public ResponseEntity<List<Attendance>> getView(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(
                schoolId, classId, sectionId, targetDate));
    }

    // ─────────────────────────────────────────────────────────────────────
    // API 5: Principal view
    // ─────────────────────────────────────────────────────────────────────
    @GetMapping("/view/{schoolId}/{classId}")
    public ResponseEntity<List<Attendance>> getAttendanceForPrincipal(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Long sectionId) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(
                schoolId, classId, sectionId, targetDate));
    }

    // ─────────────────────────────────────────────────────────────────────
    // API 6: Attendance percentage
    // ─────────────────────────────────────────────────────────────────────
    @GetMapping("/percentage/{studentId}")
    public ResponseEntity<Double> getPercentage(@PathVariable Long studentId) {
        return ResponseEntity.ok(
                attendanceService.calculateAttendancePercentage(studentId));
    }

    // ─────────────────────────────────────────────────────────────────────
    // API 7: Delete record
    // ─────────────────────────────────────────────────────────────────────
    @DeleteMapping("/delete-record/{studentId}")
    public ResponseEntity<String> deleteRecord(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        attendanceService.deleteByStudentId(studentId, date);
        return ResponseEntity.ok("Record deleted successfully.");
    }

    // ─────────────────────────────────────────────────────────────────────
    // API 8: Preview Attendance Data
    // ✅ FIX: @PathVariable String academicYear → @RequestParam Long academicYearId
    // ─────────────────────────────────────────────────────────────────────
    @GetMapping("/preview/{schoolId}/{classId}")
    public ResponseEntity<List<Attendance>> previewAttendanceForExport(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @RequestParam Long academicYearId,          // ✅ String → Long
            @RequestParam(required = false) Long sectionId) {

        List<Attendance> data = attendanceService.getAttendanceForReport(
                schoolId, classId, sectionId, academicYearId);  // ✅

        return ResponseEntity.ok(data);
    }

    // ─────────────────────────────────────────────────────────────────────
    // API 9: Export & Purge Attendance
    // ✅ FIX: @PathVariable String academicYear → @RequestParam Long academicYearId
    // ─────────────────────────────────────────────────────────────────────
    @GetMapping("/export-and-purge/{schoolId}/{classId}")
    public void exportAndPurgeAttendance(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @RequestParam Long academicYearId,          // ✅ String → Long
            @RequestParam(required = false) Long sectionId,
            HttpServletResponse response) throws IOException {

        String fileName = "Attendance_Class_" + classId
                + "_Year_" + academicYearId + ".csv";
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + fileName);

        List<Attendance> dataToExport = attendanceService.getAttendanceForReport(
                schoolId, classId, sectionId, academicYearId);  // ✅

        if (dataToExport.isEmpty()) {
            response.sendError(404, "Data nahi mila!");
            return;
        }

        PrintWriter writer = response.getWriter();
        writer.println("Date,Class,Section,Roll Number,Student Name,Status");

        for (Attendance record : dataToExport) {
            writer.println(String.format("%s,%s,%s,%s,%s,%s",
                    record.getAttendanceDate(),
                    record.getSchoolClass() != null
                            ? record.getSchoolClass().getId() : "",
                    record.getSection() != null
                            ? record.getSection().getId() : "N/A",
                    record.getRollNumber(),
                    record.getStudent() != null
                            ? record.getStudent().getName() : "",
                    record.getStatus()
            ));
        }

        writer.flush();
        writer.close();

        // ✅ Purge karo after export
        attendanceService.deleteExportedData(
                schoolId, classId, sectionId, academicYearId);
    }
}