package com.smartschool.api.serviceImpl;

import com.smartschool.api.dto.*;
import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired private UserRepository userRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("AuthService: Finding user by username: {}", request.getUsername());

        // Step 1: User find karna
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.error("User NOT FOUND in DB: {}", request.getUsername());
                    return new RuntimeException("User not found!");
                });

        // Step 2: Active check
        if (!user.isActive()) {
            log.warn("Inactive user tried to login: {}", request.getUsername());
            throw new RuntimeException("Your account is inactive. Please contact the Principal/Admin.");
        }

        // Step 3: Password check
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Wrong password attempt for user: {}", request.getUsername());
            throw new RuntimeException("Invalid Credentials!");
        }

        String token = UUID.randomUUID().toString();
        AuthResponse res = new AuthResponse();
        res.setToken(token);
        res.setRole(user.getRole().name());
        res.setUserId(user.getId());
        res.setSchoolId(user.getSchool() != null ? user.getSchool().getId() : null);
        res.setActive(user.isActive());

        if (user.getRole() == Role.ROLE_SUPER_ADMIN) {
            res.setName("Super Admin");
            log.info("Super Admin logged in.");
        }
        else if (user.getRole() == Role.ROLE_ADMIN) {
            res.setName("Principal");
            if (user.getSchool() != null) {
                res.setSchoolName(user.getSchool().getSchoolName());
                // ✅ School ka logo bhi set karo
                res.setSchoolLogo(user.getSchool().getSchoolLogo());

                // 🚩 NAYA: Academic Year fetch logic for Principal
                if (user.getSchool().getCurrentYear() != null) {
                    res.setAcademicYear(user.getSchool().getCurrentYear().getCurrentYear());
                    res.setAcademicYearId(user.getSchool().getCurrentYear().getId());
                } else {
                    res.setAcademicYear("Not Set");
                }
            }
            log.info("Principal logged in for school: {} with Year: {}", res.getSchoolName(), res.getAcademicYear());
        }
        else if (user.getRole() == Role.ROLE_TEACHER) {
            Teacher teacher = teacherRepository.findByUser(user)
                    .orElseThrow(() -> {
                        log.error("Teacher record MISSING for userId: {}", user.getId());
                        return new RuntimeException("Teacher record missing");
                    });
            res.setTeacherId(teacher.getId());
            res.setName(teacher.getName());
            res.setSchoolName(user.getSchool().getSchoolName());
            res.setSubjectExpertise(teacher.getSubjectExpertise());

            // 🚩 NAYA: Teacher ke liye bhi school ka current year set karein
            if (user.getSchool() != null && user.getSchool().getCurrentYear() != null) {
                res.setAcademicYear(user.getSchool().getCurrentYear().getCurrentYear());
                res.setAcademicYearId(user.getSchool().getCurrentYear().getId());
            }

            log.info("Teacher '{}' (id={}) logged in.", teacher.getName(), teacher.getId());
        }
        else if (user.getRole() == Role.ROLE_STUDENT) {
            Student student = studentRepository.findByUser(user)
                    .orElseThrow(() -> {
                        log.error("Student record MISSING for userId: {}", user.getId());
                        return new RuntimeException("Student record missing");
                    });
            res.setStudentId(student.getId());
            res.setName(student.getName());
            res.setRollNumber(student.getEnrollmentId());
            res.setSchoolName(user.getSchool().getSchoolName());

            if (student.getSchoolClass() != null) {
                res.setClassName(student.getSchoolClass().getClassName());
                res.setClassId(student.getSchoolClass().getId());
            }
            if (student.getSection() != null) {
                res.setSectionName(student.getSection().getSectionName());
                res.setSectionId(student.getSection().getId());
            } else {
                res.setSectionId(0L);
            }

            // 🚩 NAYA: Hardcoded "2025-26" hata kar DB se real year uthao
            if (user.getSchool() != null && user.getSchool().getCurrentYear() != null) {
                res.setAcademicYear(student.getSchool().getCurrentYear().getCurrentYear());
                res.setAcademicYearId(student.getSchool().getCurrentYear().getId());
            } else {
                res.setAcademicYear("2025-26"); // Default fallback
            }

            log.info("Student '{}' (rollNo={}) logged in.", student.getName(), student.getRollNumber());
        }

        return res;
    }

    // 🚩 NAYA: Department login method
    @Override
    public AuthResponse departmentLogin(Map<String, String> loginData) {
        log.debug("Department Login: Authenticating department");

        String departmentIdStr = loginData.get("departmentId");
        String password = loginData.get("password");

        if (departmentIdStr == null || password == null) {
            log.error("Department ID or Password missing in login request");
            throw new RuntimeException("Department ID and Password are required!");
        }

        try {
            Long departmentId = Long.parseLong(departmentIdStr);

            // Step 1: Department ID se user find karo
            User user = userRepository.findByDepartmentId(departmentId)
                    .orElseThrow(() -> {
                        log.error("Department NOT FOUND with ID: {}", departmentId);
                        return new RuntimeException("Department not found!");
                    });

            // Step 2: Active check
            if (!user.isActive()) {
                log.warn("Inactive department tried to login: {}", departmentId);
                throw new RuntimeException("This department is inactive. Please contact the Principal/Admin.");
            }

            // Step 3: Password check
            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("Wrong password attempt for department: {}", departmentId);
                throw new RuntimeException("Invalid Credentials!");
            }

            String token = UUID.randomUUID().toString();
            AuthResponse res = new AuthResponse();
            res.setToken(token);
            res.setRole(user.getRole() != null ? user.getRole().name() : "DEPARTMENT");
            res.setUserId(user.getId());
            res.setName(user.getDepartment());
            res.setDepartmentId(user.getDepartmentId());
            res.setDepartment(user.getDepartment());
            res.setSchoolId(user.getSchool() != null ? user.getSchool().getId() : null);
            res.setActive(user.isActive());

            if (user.getSchool() != null) {
                School s = user.getSchool();
                res.setSchoolName(s.getSchoolName());
                res.setSchoolLogo(s.getSchoolLogo());

                // new fields
                res.setSchoolMailId(s.getMailId());
                res.setSchoolAddress(s.getAddress());
                res.setSchoolPhoneNumber(s.getPhoneNumber());
                res.setSubscriptionStatus(s.getSubscriptionStatus() != null ? s.getSubscriptionStatus().name() : null);
                res.setSchoolCreatedAt(s.getCreatedAt());

                if (s.getState() != null) {
                    res.setStateId(s.getState().getId());
                    res.setStateName(s.getState().getName());
                }
                if (s.getDistrict() != null) {
                    res.setDistrictId(s.getDistrict().getId());
                    res.setDistrictName(s.getDistrict().getName());
                }

                if (s.getCurrentYear() != null) {
                    res.setAcademicYear(s.getCurrentYear().getCurrentYear());
                    res.setAcademicYearId(s.getCurrentYear().getId());
                }
            }

            log.info("Department '{}' (id={}) logged in successfully.", user.getDepartment(), departmentId);
            return res;

        } catch (NumberFormatException e) {
            log.error("Invalid Department ID format: {}", departmentIdStr);
            throw new RuntimeException("Invalid Department ID format!");
        }
    }
}