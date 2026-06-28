package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

/**
 * ExamResult entity — student ka ek subject ka ek exam ka result.
 *
 * Grade logic:
 *   A1 = 91%+  |  A2 = 81%+  |  B1 = 71%+  |  B2 = 61%+
 *   C1 = 51%+  |  C2 = 41%+  |  D  = 33%+  |  E  = fail
 *   AB = absent
 *
 * Pass rule: theory AUR practical DONO mein pass hona zaroori.
 *            Ek mein bhi fail = isPassed = false
 */
@Entity
@Table(name = "exam_results")
@Data
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Core links ────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_schedule_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ExamSchedule examSchedule;

    // ─── Theory marks ──────────────────────────────────────────
    private Integer marksObtainedTheory;   // teacher upload karega
    private Integer totalTheoryMarks;      // subject se copy hoga at save time

    // ─── Practical marks ───────────────────────────────────────
    // NULL agar subject.hasPractical = false
    private Integer marksObtainedPractical;
    private Integer totalPracticalMarks;

    // ─── Auto-computed fields (service mein set hoga) ──────────
    private Integer totalMarksObtained;  // theory + practical obtained
    private Integer totalMaxMarks;       // theory max + practical max
    private Double  percentage;          // (totalObt / totalMax) * 100

    // Grade: A1 / A2 / B1 / B2 / C1 / C2 / D / E / AB
    private String grade;

    // ✅ Theory AND Practical dono mein pass = true
    @Column(name = "is_passed", columnDefinition = "BIT(1) DEFAULT b'0'")
    private boolean isPassed = false;

    // ✅ Student exam mein nahi aaya
    @Column(name = "is_absent", columnDefinition = "BIT(1) DEFAULT b'0'")
    private boolean isAbsent = false;

    // ─── Audit / Meta ──────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AcademicYearConfig academicYear;

    private LocalDate uploadedAt;
    private Long      uploadedByTeacherId;
}