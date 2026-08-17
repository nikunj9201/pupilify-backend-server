package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

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
    @JoinColumn(name = "bus_fee_structure_id", nullable = false)
    private BusFeeStructure busFeeStructure;

    @Column(nullable = false)
    private double amountPaid;

    @CreationTimestamp
    private LocalDateTime paymentDate;

    private String paymentMode;
    private String receiptNumber;
}