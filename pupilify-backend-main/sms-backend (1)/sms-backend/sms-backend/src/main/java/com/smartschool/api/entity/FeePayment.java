package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class FeePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amountPaid;
    private Double totalClassFees;
    private Double remainingBalance;

    private LocalDate paymentDate;
    private String receiptNumber;
    private String paymentMode;

    // 🚩 Isme hum Student ki Unique Enrollment ID (String) save karenge
    private String rollNumber;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYearConfig academicYear;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    private Section section;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}