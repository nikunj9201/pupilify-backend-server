package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

/**
 * ExamSchedule entity.
 *
 * Ek exam entry = ek subject ka ek date pe exam.
 * examName custom hoga — e.g. "Half Yearly", "Final Exam", "Unit Test 1"
 * section optional hai — null = poori class ke liye
 */
@Entity
@Table(name = "exam_schedule")
@Data
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Custom exam name — teacher/principal khud type karega
    // e.g. "Half Yearly", "Final Exam", "Unit Test 1", "Pre-Board 2025"
    @Column(nullable = false)
    private String examName;

    private LocalDate examDate;
    private String    startTime;  // "10:00"
    private String    endTime;    // "13:00"

    // ─── Relationships ─────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SchoolClass schoolClass;

    // section null = poori class ke liye same exam
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AcademicYearConfig academicYear;

    @Column(name = "is_active", columnDefinition = "BIT(1) DEFAULT b'1'")
    private boolean isActive = true;
}