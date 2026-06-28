package com.smartschool.api.controller;

import com.smartschool.api.service.IdCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/idcard")
@CrossOrigin("*")
public class IdCardController {

    private static final Logger log = LoggerFactory.getLogger(IdCardController.class);

    @Autowired
    private IdCardService idCardService;

    // ─────────────────────────────────────────────────────────────────────────
    // API 1: Student ID Card (Single) - UPDATED FOR ENROLLMENT ID
    // URL: GET /api/idcard/student/{enrollmentId}/{schoolId}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/student/{enrollmentId}/{schoolId}")
    public ResponseEntity<byte[]> getStudentIdCard(
            @PathVariable String enrollmentId, // 🚩 Badla gaya: studentId (Long) se enrollmentId (String)
            @PathVariable Long schoolId) {

        log.info("Student ID Card request: enrollmentId={} schoolId={}", enrollmentId, schoolId);

        try {
            // Service call updated to use Enrollment ID
            byte[] htmlBytes = idCardService.generateStudentIdCard(enrollmentId, schoolId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"idcard_" + enrollmentId + ".html\"");
            headers.setContentLength(htmlBytes.length);

            log.info("Student ID Card generated successfully: enrollmentId={}", enrollmentId);
            return new ResponseEntity<>(htmlBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Student ID Card FAILED: enrollmentId={} | Error: {}", enrollmentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API 2: Teacher ID Card (Single) - (As-is)
    // URL: GET /api/idcard/teacher/{teacherId}/{schoolId}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/teacher/{teacherId}/{schoolId}")
    public ResponseEntity<byte[]> getTeacherIdCard(
            @PathVariable Long teacherId,
            @PathVariable Long schoolId) {

        log.info("Teacher ID Card request: teacherId={} schoolId={}", teacherId, schoolId);

        try {
            byte[] htmlBytes = idCardService.generateTeacherIdCard(teacherId, schoolId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"teacher_idcard_" + teacherId + ".html\"");
            headers.setContentLength(htmlBytes.length);

            log.info("Teacher ID Card generated successfully: teacherId={}", teacherId);
            return new ResponseEntity<>(htmlBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Teacher ID Card FAILED: teacherId={} | Error: {}", teacherId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API 3: Bulk Student ID Cards (Poori class ke) - (As-is)
    // URL: GET /api/idcard/student/bulk/{schoolId}/{classId}/{sectionId}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/student/bulk/{schoolId}/{classId}/{sectionId}")
    public ResponseEntity<byte[]> getBulkStudentIdCards(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @PathVariable Long sectionId) {

        log.info("BULK Student ID Cards request: schoolId={} classId={} sectionId={}",
                schoolId, classId, sectionId);

        try {
            byte[] htmlBytes = idCardService.generateBulkStudentIdCards(schoolId, classId, sectionId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"bulk_idcards_class_" + classId + ".html\"");
            headers.setContentLength(htmlBytes.length);

            log.info("Bulk Student ID Cards generated: schoolId={} classId={}", schoolId, classId);
            return new ResponseEntity<>(htmlBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Bulk ID Cards FAILED: schoolId={} classId={} | Error: {}",
                    schoolId, classId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }
}