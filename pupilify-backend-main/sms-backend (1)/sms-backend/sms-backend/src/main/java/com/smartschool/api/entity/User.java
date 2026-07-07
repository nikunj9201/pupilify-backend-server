package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Email ID user ka login username hoga

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.ORDINAL)
    private Role role; // SUPER_ADMIN, ADMIN, TEACHER, STUDENT

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = true) // Super Admin ke liye null hoga
    private School school;

    @Column(nullable = true)
    private String department; // Department name jab department create ho

    @Column(nullable = true)
    private Long departmentId; // Department ID for department login

    private boolean active = true;

    // 🚩 Compatibility helper: some parts of the code expect a User.getFullName() method.
    // We don't have a separate "fullName" column on User in this schema, so provide a safe
    // accessor that returns username (email) as a fallback. This prevents compile-time
    // "cannot find symbol: getFullName()" errors and helps reporting fallbacks.
    public String getFullName() {
        if (this.username == null || this.username.trim().isEmpty()) return "N/A";
        return this.username;
    }
}