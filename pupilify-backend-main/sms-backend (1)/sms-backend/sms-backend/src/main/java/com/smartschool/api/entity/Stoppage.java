package com.smartschool.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bus_stoppages")
@Data
public class Stoppage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stopName;


    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    @JsonIgnoreProperties("stoppages")
    private Route route;

    // Soft-delete flag - map to existing DB column if it was previously named `is_active`
    @Column(name = "is_active")
    private boolean active = true;
}