package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bus_fee_rate_structure")
@Data
public class BusFeeRateStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stoppage_id", nullable = false)
    private Stoppage stoppage;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYearConfig academicYear;

    // Monthly fee amount for this stoppage
    @Column(nullable = false)
    private double monthlyFeeAmount;

    // Number of months in the academic year
    @Column(nullable = false)
    private int totalMonths = 12;

    // Academic year start month (e.g., "July")
    @Column(nullable = false)
    private String academicYearStartMonth;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}

