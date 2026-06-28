package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "departments")
@Data
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(nullable = false)
    private String deptName; // "Admission Department", "Exam Department", "Bus Department"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentType deptType; // ADMISSION, EXAM, BUS

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = true, unique = true)
    private String username; // Department login username (manual entry)

    @Column(nullable = true)
    private String password; // Department login password (manual entry)

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
