package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "department_permissions")
@Data
public class DepartmentPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false)
    private String permissionName; // e.g., "STUDENT_ENROLLMENT", "MARKS_UPLOAD", "BUS_MANAGEMENT"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;
}

