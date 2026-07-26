package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "bus_fee_payments")
@Data
public class BusFeePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYearConfig academicYear;

    @Column(nullable = false)
    private double amountPaid;

    private LocalDate paymentDate;

    private String paymentMode;

    private String receiptNumber;

    @PrePersist
    protected void onCreate() {
        this.paymentDate = LocalDate.now();
    }
}