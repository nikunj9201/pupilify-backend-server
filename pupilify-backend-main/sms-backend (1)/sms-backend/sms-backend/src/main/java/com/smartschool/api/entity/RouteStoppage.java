package com.smartschool.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "route_stoppages")
@Data
public class RouteStoppage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stopName;

    private double latitude;
    private double longitude;

    @Column(nullable = false)
    private double monthlyFees;

    @Column(nullable = false)
    private double yearlyFees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    @JsonIgnore
    private Route route;
}