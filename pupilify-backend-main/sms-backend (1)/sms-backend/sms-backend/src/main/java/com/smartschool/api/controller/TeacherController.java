package com.smartschool.api.controller;

import com.smartschool.api.entity.Teacher;
import com.smartschool.api.service.TeacherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/admin/teachers")
@CrossOrigin("*")
public class TeacherController {

    private static final Logger log = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    private TeacherService teacherService;

    // --- Pehle wale saare methods (onboard, get, update, delete) same rahenge ---

    @PostMapping("/onboard")
    public ResponseEntity<?> onboardTeacher(
            @RequestParam("teacherData") String teacherDataJson,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("aadharImg") MultipartFile aadharImg,
            @RequestParam("passbookImg") MultipartFile passbookImg,
            @RequestParam("schoolId") Long schoolId) {
        log.info("Onboarding new teacher for schoolId: {}", schoolId);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Teacher teacher = mapper.readValue(teacherDataJson, Teacher.class);
            Teacher savedTeacher = teacherService.onboardTeacher(teacher, photo, aadharImg, passbookImg, schoolId);
            log.info("Teacher '{}' onboarded with ID: {}", savedTeacher.getName(), savedTeacher.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTeacher);
        } catch (Exception e) {
            log.error("Teacher onboarding FAILED for schoolId: {} | Error: {}", schoolId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<Teacher>> getTeachersBySchool(@PathVariable Long schoolId) {
        log.debug("Fetching teachers for schoolId: {}", schoolId);
        return ResponseEntity.ok(teacherService.getTeachersBySchool(schoolId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        log.debug("Fetching teacher by ID: {}", id);
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacherDetails) {
        log.info("Updating teacher ID: {}", id);
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacherDetails));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTeacher(@PathVariable Long id) {
        log.warn("Deleting teacher ID: {}", id);
        teacherService.deleteTeacher(id);
        log.info("Teacher deleted. ID: {}", id);
        return ResponseEntity.ok("Teacher deleted successfully!");
    }

    // --- Naye Methods: View aur Download ke liye ---

    @GetMapping("/{id}/document/{docType}")
    public ResponseEntity<Resource> getTeacherDocument(
            @PathVariable Long id,
            @PathVariable String docType,
            @RequestParam(defaultValue = "view") String mode) {

        log.info("Request to {} document '{}' for teacher ID: {}", mode, docType, id);

        try {
            // 1. Service se byte data lein
            byte[] docData = teacherService.getTeacherDocument(id, docType);

            if (docData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            ByteArrayResource resource = new ByteArrayResource(docData);

            // 2. File ka extension pata karein (Security aur Format ke liye)
            // Hum default image/jpeg rakh rahe hain, browser ise handle kar leta hai
            MediaType mediaType = MediaType.IMAGE_JPEG;
            String fileExtension = ".jpg";

            // Agar aapko PDF handle karna hai toh ye condition add kar sakte hain
            // (Optional: File path se extension nikalna zyada sahi rehta hai)

            // 3. Content-Disposition set karein (View vs Download)
            // inline = browser mein dikhayega, attachment = seedha download karega
            String contentDisposition = mode.equalsIgnoreCase("download") ? "attachment" : "inline";
            String fileName = docType + "_" + id + fileExtension;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache") // Taaki purani image cache na ho
                    .body(resource);

        } catch (Exception e) {
            log.error("Error retrieving document for teacher {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}