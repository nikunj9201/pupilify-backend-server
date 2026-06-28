package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "department_heads")
@Data
public class DepartmentHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Teacher who is assigned as department head

    @Column(nullable = false)
    private LocalDateTime assignedDate = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        assignedDate = LocalDateTime.now();
    }
}

