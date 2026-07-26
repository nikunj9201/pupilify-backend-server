package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "student_bus_assignments")
@Data
public class StudentBusAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "stoppage_id", nullable = false)
    private Stoppage stoppage;

    @ManyToOne
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYearConfig academicYear;

    @Column(nullable = false)
    private double transportFee;

    private LocalDate assignmentDate;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        this.assignmentDate = LocalDate.now();
        this.active = true;
    }
}