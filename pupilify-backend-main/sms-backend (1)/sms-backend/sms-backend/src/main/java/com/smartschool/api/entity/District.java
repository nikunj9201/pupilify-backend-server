package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "districts")
@Data
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String code;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    private LocalDateTime createdAt = LocalDateTime.now();
}

