package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_bus_assignments")
@Data
public class StudentBusAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "boarding_point_id")
    private BusBoardingPoint boardingPoint;

    private Long academicYearId;

    private LocalDateTime assignedAt = LocalDateTime.now();

    private boolean active = true;
}

