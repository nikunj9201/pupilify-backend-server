package com.smartschool.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "bus_routes")
@Data
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String routeName;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    @JsonIgnoreProperties("routes")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("route")
    private List<Stoppage> stoppages;

    private boolean isActive = true;
}