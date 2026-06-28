package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "principal_department_assignments")
@Data
public class PrincipalDepartmentAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "principal_user_id")
    private User principal;

    @ManyToOne
    @JoinColumn(name = "staff_user_id")
    private User staff;

    @Column(nullable = false)
    private String department;

    private LocalDateTime assignedAt = LocalDateTime.now();

    private boolean active = true;
}

