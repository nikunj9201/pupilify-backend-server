package com.smartschool.api.controller;

import com.smartschool.api.entity.Student;
import com.smartschool.api.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/students")
@CrossOrigin("*")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @GetMapping("/export-excel/{schoolId}")
    public ResponseEntity<?> exportStudentsToExcel(
            @PathVariable Long schoolId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam Long academicYearId) {
        try {
            List<com.smartschool.api.dto.StudentExcelDTO> students = studentService.getFilteredStudentsForExcel(schoolId, classId, sectionId, academicYearId);
            if (students.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No students found for the selected criteria.");
            }
            byte[] excelData = studentService.generateStudentReportExcel(students);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "students.xlsx");

            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error exporting students to Excel: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ─────────────────────────────────────────────────────────
    // 1. ONBOARDING (All files are optional)
    // ─────────────────────────────────────────────────────────
    @PostMapping("/onboard/{schoolId}/{classId}/{sectionId}")
    public ResponseEntity<?> onboardStudent(
            @PathVariable Long schoolId, @PathVariable Long classId, @PathVariable Long sectionId,
            @RequestParam("studentData") String studentDataJson,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "marksheet", required = false) MultipartFile marksheet,
            @RequestParam(value = "tc", required = false) MultipartFile tc,
            @RequestParam(value = "aadharImg", required = false) MultipartFile aadharImg,
            @RequestParam(value = "samagraImg", required = false) MultipartFile samagraImg,
            @RequestParam(value = "passbookImg", required = false) MultipartFile passbookImg,
            @RequestParam(value = "apaarImg", required = false) MultipartFile apaarImg,
            @RequestParam(value = "birthCertificate", required = false) MultipartFile birthCertificate,
            @RequestParam(value = "incomeCertificate", required = false) MultipartFile incomeCertificate,
            @RequestParam(value = "castCertificate", required = false) MultipartFile castCertificate,
            @RequestParam(value = "domicileCertificate", required = false) MultipartFile domicileCertificate,
            @RequestParam("academicYearId") Long academicYearId) {

        log.info("Onboarding student for schoolId:{} with Year ID:{}", schoolId, academicYearId);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Student student = mapper.readValue(studentDataJson, Student.class);

            if (student.getName() == null || student.getEmail() == null || student.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Name, Email and Password are mandatory!");
            }

            Student savedStudent = studentService.onboardStudentWithApaar(
                    student, photo, marksheet, tc, aadharImg, samagraImg,
                    passbookImg, apaarImg, birthCertificate, incomeCertificate, castCertificate, domicileCertificate, schoolId, classId, sectionId, academicYearId);

            return ResponseEntity.status(HttpStatus.CREATED).body(enrichStudentWithUrls(savedStudent));
        } catch (Exception e) {
            log.error("Error onboarding student: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // 🚩 NAYA ADDED: STUDENT EDIT/UPDATE API 🚩
    // ─────────────────────────────────────────────────────────
    // 1.1 UPDATE/EDIT STUDENT DETAILS & FILES
    // ─────────────────────────────────────────────────────────
    // ✅ UPDATE KR SAKTE HO:
    //    - phoneNo, address, gender, caste
    //    - fatherName, motherName, fatherContactNumber
    //    - aadharCardNo, samagraId, rollNumber
    //    - Sab documents/images (photo, marksheet, tc, aadhar, samagra, passbook, apaar)
    //
    // ❌ UPDATE NAHI KR SAKTE (Onboarding ke time se set):
    //    - name, email, password, dob
    //    - school, class, section (Ye promotion API se change hote hain)
    // ─────────────────────────────────────────────────────────
    @PutMapping("/update/{studentId}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long studentId,
            @RequestParam("studentData") String studentDataJson,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "marksheet", required = false) MultipartFile marksheet,
            @RequestParam(value = "tc", required = false) MultipartFile tc,
            @RequestParam(value = "aadharImg", required = false) MultipartFile aadharImg,
            @RequestParam(value = "samagraImg", required = false) MultipartFile samagraImg,
            @RequestParam(value = "passbookImg", required = false) MultipartFile passbookImg,
            @RequestParam(value = "apaarImg", required = false) MultipartFile apaarImg,
            @RequestParam(value = "birthCertificate", required = false) MultipartFile birthCertificate,
            @RequestParam(value = "incomeCertificate", required = false) MultipartFile incomeCertificate,
            @RequestParam(value = "castCertificate", required = false) MultipartFile castCertificate,
            @RequestParam(value = "domicileCertificate", required = false) MultipartFile domicileCertificate) {

        log.info("Updating student details for ID: {}", studentId);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Student studentUpdateInfo = mapper.readValue(studentDataJson, Student.class);

            Student updatedStudent = studentService.updateStudent(
                    studentId, studentUpdateInfo, photo, marksheet, tc, aadharImg, samagraImg,
                    passbookImg, apaarImg, birthCertificate, incomeCertificate, castCertificate, domicileCertificate);

            return ResponseEntity.ok(enrichStudentWithUrls(updatedStudent));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Invalid JSON data for student update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid student data JSON - " + e.getMessage());
        } catch (java.io.IOException e) {
            log.error("Error processing file upload for student ID {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: File processing failed - " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error updating student ID {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error updating student ID {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An unexpected error occurred - " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    // 2. ADVANCED FILTER REPORT API
    // ─────────────────────────────────────────────────────────
    @GetMapping("/report/filter/{schoolId}")
    public ResponseEntity<List<Student>> getFilteredStudentReport(
            @PathVariable Long schoolId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String caste,
            @RequestParam Long academicYearId) {

        log.info("Generating student report for School:{} | Class:{} | Section:{} | Gender:{} | Caste:{}",
                schoolId, classId, sectionId, gender, caste);

        List<Student> students = studentService.getFilteredStudents(schoolId, classId, sectionId, gender, caste, academicYearId);

        List<Student> enrichedList = students.stream()
                .map(this::enrichStudentWithUrls)
                .collect(Collectors.toList());

        return ResponseEntity.ok(enrichedList);
    }

    // ─────────────────────────────────────────────────────────
    // 3. ALL STUDENTS BY SCHOOL ID
    // ─────────────────────────────────────────────────────────
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<Student>> getAllStudentsBySchool(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId) {
        log.info("Fetching all students for School ID:{} and Year ID:{}", schoolId, academicYearId);

        List<Student> students = studentService.getFilteredStudents(schoolId, null, null, null, null, academicYearId);

        List<Student> enrichedList = students.stream()
                .map(this::enrichStudentWithUrls)
                .collect(Collectors.toList());

        return ResponseEntity.ok(enrichedList);
    }

    // ─────────────────────────────────────────────────────────
    // 4. STATS & LISTING
    // ─────────────────────────────────────────────────────────
    @GetMapping("/school/{schoolId}/class/{classId}")
    public ResponseEntity<List<Student>> getStudentsByClass(
            @PathVariable Long schoolId, @PathVariable Long classId, @RequestParam Long academicYearId) {
        List<Student> students = studentService.getStudentsByClass(schoolId, classId, academicYearId);
        return ResponseEntity.ok(students.stream().map(this::enrichStudentWithUrls).collect(Collectors.toList()));
    }

    @GetMapping("/school/{schoolId}/class/{classId}/section/{sectionId}")
    public ResponseEntity<List<Student>> getStudentsByClassAndSection(
            @PathVariable Long schoolId, @PathVariable Long classId, @PathVariable Long sectionId, @RequestParam Long academicYearId) {
        List<Student> students = studentService.getStudentsByClassAndSection(schoolId, classId, sectionId, academicYearId);
        return ResponseEntity.ok(students.stream().map(this::enrichStudentWithUrls).collect(Collectors.toList()));
    }

    @PutMapping("/update/enrollment/{enrollmentId}/{classId}/{sectionId}")
    public ResponseEntity<?> updateStudentPromotion(
            @PathVariable String enrollmentId,
            @PathVariable Long classId,
            @PathVariable Long sectionId,
            @RequestParam Long academicYearId) {

        log.info("Promoting Student Enrollment ID: {} to Class: {} Section: {}", enrollmentId, classId, sectionId);

        try {
            Long finalSectionId = (sectionId != null && sectionId > 0) ? sectionId : null;
            Student updated = studentService.promoteStudent(enrollmentId, classId, finalSectionId, academicYearId);
            return ResponseEntity.ok(enrichStudentWithUrls(updated));
        } catch (Exception e) {
            log.error("Error promoting student: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/stats/caste/{schoolId}")
    public ResponseEntity<List<Map<String, Object>>> getCasteStats(@PathVariable Long schoolId, @RequestParam Long academicYearId) {
        return ResponseEntity.ok(studentService.getCasteStatistics(schoolId, academicYearId));
    }

    @GetMapping("/stats/gender/{schoolId}")
    public ResponseEntity<List<Map<String, Object>>> getGenderStats(@PathVariable Long schoolId, @RequestParam Long academicYearId) {
        return ResponseEntity.ok(studentService.getGenderStatistics(schoolId, academicYearId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(enrichStudentWithUrls(student));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student inactivated successfully.");
    }

    private Student enrichStudentWithUrls(Student student) {
        if (student == null) return null;

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/admin/students/files/")
                .toUriString();

        student.setStudentPhoto(buildFullUrl(baseUrl, student.getStudentPhoto()));
        student.setLastClassMarksheet(buildFullUrl(baseUrl, student.getLastClassMarksheet()));
        student.setTcImage(buildFullUrl(baseUrl, student.getTcImage()));
        student.setAadharCardImage(buildFullUrl(baseUrl, student.getAadharCardImage()));
        student.setSamagraIdImage(buildFullUrl(baseUrl, student.getSamagraIdImage()));
        student.setBankPassbookImage(buildFullUrl(baseUrl, student.getBankPassbookImage()));
        student.setApaarCardImage(buildFullUrl(baseUrl, student.getApaarCardImage()));
        student.setBirthCertificate(buildFullUrl(baseUrl, student.getBirthCertificate()));
        student.setIncomeCertificate(buildFullUrl(baseUrl, student.getIncomeCertificate()));
        student.setCastCertificate(buildFullUrl(baseUrl, student.getCastCertificate()));
        student.setDomicileCertificate(buildFullUrl(baseUrl, student.getDomicileCertificate()));

        return student;
    }

    private String buildFullUrl(String baseUrl, String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) return null;

        if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
            if (fileName.contains("/api/admin/students/files/")) {
                String pureFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                return baseUrl + pureFileName;
            }
            return fileName;
        }

        return baseUrl + fileName;
    }
}