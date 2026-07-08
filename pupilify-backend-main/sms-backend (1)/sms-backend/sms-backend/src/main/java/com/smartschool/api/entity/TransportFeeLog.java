package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "transport_fee_logs")
@Data
public class TransportFeeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYearConfig academicYear;

    @Column(nullable = false)
    private String monthYear; // e.g., "July 2026"

    @Column(nullable = false)
    private double amountDue;

    private double amountPaid = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeStatus status;

    private LocalDate paymentDate;

    private String paymentMode;

    public enum FeeStatus {
        DUE,
        PAID,
        PARTIALLY_PAID
    }

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = FeeStatus.DUE;
        }
    }
}