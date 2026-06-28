package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "bus_boarding_points")
@Data
public class BusBoardingPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @Column(nullable = false)
    private String name;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private Short seq;
}

