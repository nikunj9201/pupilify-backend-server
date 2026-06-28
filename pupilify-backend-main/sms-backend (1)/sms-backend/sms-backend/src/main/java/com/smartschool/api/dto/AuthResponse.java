package com.smartschool.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    private Long schoolId;
    private Long userId;
    private String message;
    private Long teacherId;
    private Long studentId;

    // ✅ NEW — Timetable ke liye zaruri fields
    private Long   classId;       // Student ki class ID
    private Long   sectionId;     // Student ka section ID (0 = no section / Section A)
    private String academicYear;  // e.g. "2025-26"

     // Isme 2025-26 ya 1999-20 jayega
    private Long academicYearId;

    private String name;
    private String schoolName;
    private boolean active;

    // AuthResponse.java mein yeh field add karo
    private String schoolLogo;

    private String className;
    private String sectionName;
    private String rollNumber;
    private String subjectExpertise;

    // 🚩 NAYA: Department login ke liye fields
    private Long departmentId;
    private String department;

    // ─────────────────────────────────────────────────────────
    // Full School details (added so department login can return all school info)
    // ─────────────────────────────────────────────────────────
    private String schoolMailId;       // school's email/contact
    private String schoolAddress;
    private String schoolPhoneNumber;
    private String subscriptionStatus; // e.g. TRIAL, ACTIVE
    private LocalDateTime schoolCreatedAt;

    private Long stateId;
    private String stateName;
    private Long districtId;
    private String districtName;
}