package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "student_transport_mappings")
@Data
public class StudentTransportMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stoppage_id", nullable = false)
    private RouteStoppage stoppage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    public enum FeeType {
        MONTHLY,
        YEARLY
    }
}