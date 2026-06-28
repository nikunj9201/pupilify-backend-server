package com.smartschool.api.controller;

import com.smartschool.api.entity.Department;
import com.smartschool.api.entity.DepartmentPermission;
import com.smartschool.api.entity.User;
import com.smartschool.api.repository.DepartmentPermissionRepository;
import com.smartschool.api.repository.DepartmentRepository;
import com.smartschool.api.repository.UserRepository;
import com.smartschool.api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/department-login")
@CrossOrigin("*")
public class DepartmentLoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentPermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> departmentLogin(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
            }

            // Find user by username
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
            }

            User foundUser = userOpt.get();

            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
            }

            // Check if user is assigned to a department
            if (foundUser.getDepartmentId() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "User is not assigned to any department"));
            }

            // Get Department details
            Optional<Department> departmentOpt = departmentRepository.findById(foundUser.getDepartmentId());
            if (departmentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Department not found"));
            }

            Department dept = departmentOpt.get();

            // Get Department Permissions
            List<DepartmentPermission> permissions = permissionRepository.findByDepartmentIdAndIsActiveTrue(dept.getId());
            List<String> permissionNames = permissions.stream()
                    .map(DepartmentPermission::getPermissionName)
                    .collect(Collectors.toList());

            // ✅ Generate JWT TOKEN (not UUID)
            String token = jwtUtil.generateToken(
                foundUser.getUsername(),
                "ROLE_DEPARTMENT",
                dept.getId()
            );

            // Build response
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("token", token);

            // User object
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", foundUser.getId());
            userMap.put("username", foundUser.getUsername());
            userMap.put("role", "ROLE_DEPARTMENT");
            responseMap.put("user", userMap);

            // Department object
            Map<String, Object> deptMap = new HashMap<>();
            deptMap.put("id", dept.getId());
            deptMap.put("name", dept.getDeptName());
            deptMap.put("type", dept.getDeptType() != null ? dept.getDeptType().toString() : "ACCOUNTS");
            deptMap.put("schoolId", dept.getSchool().getId());
            deptMap.put("schoolName", dept.getSchool().getSchoolName());
            deptMap.put("schoolLogo", dept.getSchool().getSchoolLogo());
            responseMap.put("department", deptMap);

            // Add permissions to response
            responseMap.put("permissions", permissionNames);

            // School object
            Map<String, Object> schoolMap = new HashMap<>();
            schoolMap.put("id", dept.getSchool().getId());
            schoolMap.put("name", dept.getSchool().getSchoolName());
            schoolMap.put("logo", dept.getSchool().getSchoolLogo());
            schoolMap.put("mailId", dept.getSchool().getMailId());
            schoolMap.put("address", dept.getSchool().getAddress());
            schoolMap.put("phoneNumber", dept.getSchool().getPhoneNumber());
            schoolMap.put("subscriptionStatus", dept.getSchool().getSubscriptionStatus());
            schoolMap.put("createdAt", dept.getSchool().getCreatedAt());
            
            if (dept.getSchool().getState() != null) {
                Map<String, Object> stateMap = new HashMap<>();
                stateMap.put("id", dept.getSchool().getState().getId());
                stateMap.put("name", dept.getSchool().getState().getName());
                schoolMap.put("state", stateMap);
            }
            
            if (dept.getSchool().getDistrict() != null) {
                Map<String, Object> districtMap = new HashMap<>();
                districtMap.put("id", dept.getSchool().getDistrict().getId());
                districtMap.put("name", dept.getSchool().getDistrict().getName());
                schoolMap.put("district", districtMap);
            }
            
            responseMap.put("school", schoolMap);

            // Academic Year object
            if (dept.getSchool().getCurrentYear() != null) {
                Map<String, Object> academicYearMap = new HashMap<>();
                academicYearMap.put("id", dept.getSchool().getCurrentYear().getId());
                academicYearMap.put("year", dept.getSchool().getCurrentYear().getCurrentYear());
                responseMap.put("academicYear", academicYearMap);
            }

            responseMap.put("message", "Login successful");

            System.out.println("✅ Department login successful for: " + foundUser.getUsername());
            System.out.println("🔑 Token generated: " + token.substring(0, 50) + "...");

            return ResponseEntity.ok(responseMap);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || (!token.startsWith("Bearer ") && !token.startsWith("Bearer"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token format"));
            }

            String actualToken = token.replace("Bearer ", "").trim();
            boolean isValid = jwtUtil.validateToken(actualToken);
            
            if (isValid) {
                String username = jwtUtil.extractUsername(actualToken);
                String role = jwtUtil.getRoleFromToken(actualToken);
                return ResponseEntity.ok(Map.of(
                    "valid", true, 
                    "username", username,
                    "role", role,
                    "message", "Token is valid"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "error", "Invalid token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token verification failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);
            Long deptId = jwtUtil.getDepartmentIdFromToken(token);
            
            Optional<Department> department = departmentRepository.findById(deptId);
            if (department.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Department not found"));
            }

            return ResponseEntity.ok(Map.of(
                "username", username,
                "department", Map.of(
                    "id", department.get().getId(),
                    "name", department.get().getDeptName(),
                    "type", department.get().getDeptType(),
                    "schoolId", department.get().getSchool().getId(),
                    "schoolName", department.get().getSchool().getSchoolName()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}