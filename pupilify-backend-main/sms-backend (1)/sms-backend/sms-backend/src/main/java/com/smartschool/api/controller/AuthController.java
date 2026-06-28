package com.smartschool.api.controller;

import com.smartschool.api.dto.AuthResponse;
import com.smartschool.api.dto.LoginRequest;
import com.smartschool.api.dto.LoginResponse;
import com.smartschool.api.dto.ResetPasswordRequest;
import com.smartschool.api.entity.School;
import com.smartschool.api.entity.User;
import com.smartschool.api.entity.Teacher;
import com.smartschool.api.entity.Student;
import com.smartschool.api.repository.SchoolRepository;
import com.smartschool.api.repository.UserRepository;
import com.smartschool.api.repository.TeacherRepository;
import com.smartschool.api.repository.StudentRepository;
import com.smartschool.api.service.AuthService;
import com.smartschool.api.service.StateManagerService;
import com.smartschool.api.service.DistrictManagerService;
import com.smartschool.api.service.PasswordResetService;
import com.smartschool.api.security.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping({"/api/auth", "/auth"})
@CrossOrigin("*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StateManagerService stateManagerService;

    @Autowired
    private DistrictManagerService districtManagerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private PasswordResetService resetService;

    // ================= NORMAL LOGIN (ADMIN/PRINCIPAL/TEACHER/STUDENT) =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request == null || request.getUsername() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username missing"));
        }

        log.info("Login attempt for username: {}", request.getUsername());

        try {
            // Find user by username
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            
            if (userOpt.isEmpty()) {
                log.error("User not found: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
            }

            User user = userOpt.get();

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.error("Invalid password for user: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
            }

            // ✅ FIX: Get school from user - User entity has getSchool() method, not getSchoolId()
            School school = user.getSchool();
            
            // If school is null, try to find by schoolId if it exists
            Long schoolIdValue = null;
            if (school == null) {
                // Try to get schoolId from user (if your User entity has a schoolId field)
                // Otherwise, schoolId will be null
                log.warn("User {} has no associated school", user.getUsername());
            } else {
                schoolIdValue = school.getId();
            }

            // ✅ Generate JWT token with proper role
            String role = user.getRole().name();
            String fullRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            String token = jwtUtil.generateToken(user.getUsername(), fullRole, null);

            log.info("✅ Token generated for user: {}, Role: {}", user.getUsername(), fullRole);

            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("role", fullRole);
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("message", "Login successful");

            // Add schoolId if available
            if (schoolIdValue != null) {
                response.put("schoolId", schoolIdValue);
            }

            // Add school details
            if (school != null) {
                Map<String, Object> schoolMap = new HashMap<>();
                schoolMap.put("id", school.getId());
                schoolMap.put("name", school.getSchoolName());
                schoolMap.put("logo", school.getSchoolLogo());
                schoolMap.put("mailId", school.getMailId());
                schoolMap.put("address", school.getAddress());
                schoolMap.put("phoneNumber", school.getPhoneNumber());
                schoolMap.put("subscriptionStatus", school.getSubscriptionStatus());
                response.put("school", schoolMap);

                // Add academic year
                if (school.getCurrentYear() != null) {
                    Map<String, Object> academicYearMap = new HashMap<>();
                    academicYearMap.put("id", school.getCurrentYear().getId());
                    academicYearMap.put("year", school.getCurrentYear().getCurrentYear());
                    response.put("academicYear", academicYearMap);
                }
            }

            // 🎓 ADD TEACHER DETAILS IF USER IS TEACHER
            if (fullRole.equals("ROLE_TEACHER")) {
                Optional<Teacher> teacherOpt = teacherRepository.findByUser(user);
                if (teacherOpt.isPresent()) {
                    Teacher teacher = teacherOpt.get();
                    Map<String, Object> teacherMap = new HashMap<>();
                    teacherMap.put("teacherId", teacher.getId());
                    teacherMap.put("name", teacher.getName());
                    teacherMap.put("email", teacher.getMail());
                    teacherMap.put("phoneNumber", teacher.getPhoneNumber());
                    teacherMap.put("alternateNumber", teacher.getAlternateNumber());
                    teacherMap.put("qualification", teacher.getQualification());
                    teacherMap.put("subjectExpertise", teacher.getSubjectExpertise());
                    teacherMap.put("address", teacher.getAddress());
                    teacherMap.put("dob", teacher.getDob());
                    teacherMap.put("aadharNo", teacher.getAadharNo());
                    teacherMap.put("salary", teacher.getSalary());
                    teacherMap.put("teacherPhoto", teacher.getTeacherPhoto());
                    teacherMap.put("aadharImage", teacher.getAadharImage());
                    teacherMap.put("bankPassbookImage", teacher.getBankPassbookImage());
                    teacherMap.put("active", teacher.isActive());
                    response.put("teacher", teacherMap);
                }
            }

            // 👨‍🎓 ADD STUDENT DETAILS IF USER IS STUDENT
            if (fullRole.equals("ROLE_STUDENT")) {
                Optional<Student> studentOpt = studentRepository.findByUser(user);
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    Map<String, Object> studentMap = new HashMap<>();
                    studentMap.put("studentId", student.getId());
                    studentMap.put("name", student.getName());
                    studentMap.put("email", student.getEmail());
                    studentMap.put("phoneNo", student.getPhoneNo());
                    studentMap.put("enrollmentId", student.getEnrollmentId());
                    studentMap.put("rollNumber", student.getRollNumber());
                    studentMap.put("gender", student.getGender());
                    studentMap.put("address", student.getAddress());
                    studentMap.put("dob", student.getDob());
                    studentMap.put("fatherName", student.getFatherName());
                    studentMap.put("motherName", student.getMotherName());
                    studentMap.put("fatherContactNumber", student.getFatherContactNumber());
                    studentMap.put("caste", student.getCaste());
                    studentMap.put("apaarId", student.getApaarId());
                    studentMap.put("aadharCardNo", student.getAadharCardNo());
                    studentMap.put("samagraId", student.getSamagraId());
                    studentMap.put("studentPhoto", student.getStudentPhoto());
                    studentMap.put("aadharCardImage", student.getAadharCardImage());
                    studentMap.put("samagraIdImage", student.getSamagraIdImage());
                    studentMap.put("bankPassbookImage", student.getBankPassbookImage());
                    studentMap.put("apaarCardImage", student.getApaarCardImage());
                    studentMap.put("tcImage", student.getTcImage());
                    studentMap.put("lastClassMarksheet", student.getLastClassMarksheet());
                    studentMap.put("active", student.isActive());

                    // Class और Section details भी add करें
                    if (student.getSchoolClass() != null) {
                        Map<String, Object> classMap = new HashMap<>();
                        classMap.put("classId", student.getSchoolClass().getId());
                        classMap.put("className", student.getSchoolClass().getClassName());
                        studentMap.put("class", classMap);
                    }

                    if (student.getSection() != null) {
                        Map<String, Object> sectionMap = new HashMap<>();
                        sectionMap.put("sectionId", student.getSection().getId());
                        sectionMap.put("sectionName", student.getSection().getSectionName());
                        studentMap.put("section", sectionMap);
                    }

                    response.put("student", studentMap);
                }
            }

            log.info("✅ Login successful for user: {} with role: {}", request.getUsername(), fullRole);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "message", e.getMessage()));
        }
    }

    // ================= MANAGER LOGIN (STATE/DISTRICT MANAGER) =================
    @PostMapping("/manager-login")
    public ResponseEntity<?> managerLogin(@RequestBody LoginRequest request) {

        if (request == null || request.getUsername() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email missing"));
        }

        log.info("Manager login attempt for email: {}", request.getUsername());

        try {
            // ===== STATE MANAGER =====
            var stateManager = stateManagerService.getByEmail(request.getUsername());
            if (stateManager.isPresent()) {
                var manager = stateManager.get();

                if (passwordEncoder.matches(request.getPassword(), manager.getPassword())
                        && manager.getActive()) {

                    String role = manager.getRole();
                    String fullRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    String token = jwtUtil.generateToken(manager.getEmail(), fullRole, null);

                    log.info("✅ State Manager login successful: {}", manager.getEmail());

                    LoginResponse response = new LoginResponse();
                    response.setId(manager.getId());
                    response.setEmail(manager.getEmail());
                    response.setRole(fullRole);
                    response.setName(manager.getName());
                    response.setActive(manager.getActive());
                    response.setMessage("Login successful");
                    response.setSuccess(true);
                    response.setToken(token);

                    return ResponseEntity.ok(response);
                }
            }

            // ===== DISTRICT MANAGER =====
            var districtManager = districtManagerService.getByEmail(request.getUsername());
            if (districtManager.isPresent()) {
                var manager = districtManager.get();

                if (passwordEncoder.matches(request.getPassword(), manager.getPassword())
                        && manager.getActive()) {

                    String role = manager.getRole();
                    String fullRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    String token = jwtUtil.generateToken(manager.getEmail(), fullRole, null);

                    log.info("✅ District Manager login successful: {}", manager.getEmail());

                    LoginResponse response = new LoginResponse();
                    response.setId(manager.getId());
                    response.setEmail(manager.getEmail());
                    response.setRole(fullRole);
                    response.setName(manager.getName());
                    response.setActive(manager.getActive());
                    response.setMessage("Login successful");
                    response.setSuccess(true);
                    response.setToken(token);

                    return ResponseEntity.ok(response);
                }
            }

            log.error("❌ Manager login failed for email: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password", "message", "Authentication failed"));

        } catch (Exception e) {
            log.error("Manager login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage(), "message", "Authentication failed"));
        }
    }

    // ================= REFRESH TOKEN =================
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Authorization header missing"));
        }

        String jwt = (token.startsWith("Bearer "))
                ? token.substring(7)
                : token;

        try {
            String username = jwtUtil.extractUsername(jwt);

            if (username == null || username.isEmpty()) {
                throw new RuntimeException("Invalid token - cannot extract username");
            }

            // ✅ IMPORTANT: Verify user still exists in database
            try {
                var userOpt = userRepository.findByUsername(username);
                if (userOpt.isEmpty()) {
                    log.warn("❌ User not found in database during refresh (possible database reset): {}", username);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "User not found", "message", "Database may have been reset. Please log in again."));
                }
            } catch (Exception e) {
                log.error("❌ Error checking user existence: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User verification failed", "message", "Please log in again."));
            }

            // Get role from old token
            String role = jwtUtil.getRoleFromToken(jwt);
            
            // Generate new token
            String newToken = jwtUtil.generateToken(username, role, null);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", newToken);
            response.put("message", "Token refreshed successfully");

            log.info("✅ Token refreshed successfully for username: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token", "message", e.getMessage()));
        }
    }

    // ================= FORGOT PASSWORD =================
   @PostMapping("/forgot-password")
    public ResponseEntity<String> sendOtpForReset(@RequestParam String email) {
        log.info("Requesting OTP for email: {}", email);
        try {
            resetService.sendOtp(email);
            return ResponseEntity.ok("OTP has been sent to your registered email address.");
        } catch (Exception e) {
            log.error("Failed to send OTP: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ================= RESET PASSWORD =================
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        log.info("Attempting to reset password for email: {}", request.getEmail());
        try {
            resetService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
            return ResponseEntity.ok("Password has been reset successfully. You can now login with your new password.");
        } catch (Exception e) {
            log.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ================= VERIFY TOKEN =================
    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = (token.startsWith("Bearer ")) ? token.substring(7) : token;
            boolean isValid = jwtUtil.validateToken(jwt);
            
            if (isValid) {
                String username = jwtUtil.extractUsername(jwt);
                String role = jwtUtil.getRoleFromToken(jwt);
                
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                response.put("role", role);
                response.put("message", "Token is valid");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "error", "Invalid token"));
            }
        } catch (Exception e) {
            log.error("Token verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Token verification failed"));
        }
    }
}
