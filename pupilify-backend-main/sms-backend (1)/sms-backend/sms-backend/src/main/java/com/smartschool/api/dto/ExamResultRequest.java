package com.smartschool.api.dto;

import lombok.Data;

/**
 * Request DTO — teacher bulk upload ke liye yahi body bhejta hai.
 *
 * Frontend se ek array aata hai — ek item per student.
 *
 * Rules:
 *  - isAbsent = true  → marks fields null/ignore kar do
 *  - isAbsent = false → marksObtainedTheory hamesha required
 *  - isAbsent = false + subject.hasPractical = true
 *                     → marksObtainedPractical bhi required
 */
@Data
public class ExamResultRequest {

    private Long    studentId;    // Required — kis student ke marks hain
    private Long    teacherId;    // Required — kaun upload kar raha hai

    // ✅ Agar student exam mein nahi aaya toh true karo
    // True hone pe marks 0, grade = "AB", isPassed = false set hoga
    private boolean isAbsent = false;

    // Theory marks — isAbsent = false hone pe zaroori
    private Integer marksObtainedTheory;

    // Practical marks — SIRF TAB bhejo jab subject.hasPractical = true
    // Subject theory-only hai toh is field ko bilkul mat bhejo (null rahne do)
    private Integer marksObtainedPractical;
}