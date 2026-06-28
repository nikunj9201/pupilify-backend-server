package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "district_managers")
@Data
public class DistrictManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "state_id", nullable = false)
    private Long stateId;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }
}
