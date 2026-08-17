package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bus_fee_structure")
@Data
public class BusFeeStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYearConfig academicYear;

    @Column(nullable = false)
    private String month;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private double amount;
}