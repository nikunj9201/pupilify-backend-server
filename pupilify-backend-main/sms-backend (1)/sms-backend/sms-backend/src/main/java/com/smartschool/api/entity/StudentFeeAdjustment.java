package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "student_fee_adjustments")
@Data
public class StudentFeeAdjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYearConfig academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeAdjustmentType feeType;

    @Column(nullable = false)
    private Double amount;

    private String description;

    @Column(nullable = false)
    private boolean isArchived = false;

    public enum FeeAdjustmentType {
        ADMISSION_FEE,
        PENALTY,
        DISCOUNT,
        EXTRA_FEE
    }
}