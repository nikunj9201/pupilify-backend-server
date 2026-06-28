package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Subject entity.
 *
 * hasPractical = false  →  sirf theory marks, practical fields NULL rahenge
 * hasPractical = true   →  theory + practical dono fields fill karo
 */
@Entity
@Table(name = "subjects")
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subjectName;         // "Mathematics", "Science Lab"

    private String subjectCode;         // "MATH-10", "SCI-PRAC-10"

    // ─── Theory Marks ──────────────────────────────────────────
    @Column(nullable = false)
    private Integer totalTheoryMarks;   // e.g. 80

    @Column(nullable = false)
    private Integer passingTheoryMarks; // e.g. 27

    // ─── Practical Marks (optional) ────────────────────────────
    // Subject create karte waqt decide hoga: hasPractical true/false
    @Column(nullable = false, columnDefinition = "BIT(1) DEFAULT b'0'")
    private boolean hasPractical = false;

    // NULL rahega agar hasPractical = false
    private Integer totalPracticalMarks;    // e.g. 20
    private Integer passingPracticalMarks;  // e.g. 7

    // ─── Relationships ─────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AcademicYearConfig academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Section section;

    @Column(name = "is_active", columnDefinition = "BIT(1) DEFAULT b'1'")
    private boolean isActive = true;
}