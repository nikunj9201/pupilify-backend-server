package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "student_monthly_fee_structure")
@Data
public class StudentMonthlyFeeStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

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

    @Column(nullable = false)
    private double monthlyFeeAmount; // Fee per month from stoppage

    // JSON array of months: ["July", "August", "September", ...]
    @Column(columnDefinition = "JSON")
    private String selectedMonths;

    // Month from which student joined (for late joining calculation)
    // If student joins from 7th month in a 12-month academic year, fees = monthlyFeeAmount * remaining months
    @Column(nullable = false)
    private String joiningMonth; // e.g., "July"

    @Column(nullable = false)
    private int joiningYear; // Academic year

    // Payment frequency: MONTHLY or YEARLY
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentFrequency paymentFrequency = PaymentFrequency.MONTHLY;

    // Calculated total fee based on selected months and joining month
    @Column(nullable = false)
    private double totalFeeAmount;

    @Column(nullable = false)
    private LocalDate assignmentDate = LocalDate.now();

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public enum PaymentFrequency {
        MONTHLY,
        YEARLY
    }
}

