package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FeeStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalFees;
    private boolean isActive = true;
    // 🚩 NEW: Entity-based Academic Year Mapping [cite: 79, 349]
    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYearConfig academicYear;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    private Section section;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
}