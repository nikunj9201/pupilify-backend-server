package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "bus_fees")
@Data
public class BusFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @Column(nullable = false)
    private BigDecimal amount;

    private Long academicYearId;

    private String feeType = "BUS";

    private LocalDateTime createdAt = LocalDateTime.now();
}

