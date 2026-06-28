package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "academic_year_config")
@Data
public class AcademicYearConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String currentYear;   // e.g. "2026-27"

    private String previousYear;  // e.g. "2025-26"

    private LocalDateTime changedAt;
    private Long changedByUserId;

    // ✅ FIX: School-specific year — sirf us school ka change hoga
    @ManyToOne
    @JoinColumn(name = "school_id")
    @JsonIgnoreProperties({"users", "currentYear"})
    private School school;

    @Enumerated(EnumType.STRING)
    private YearChangeStatus status = YearChangeStatus.ACTIVE;

    private boolean emailSent = false;
    private int emailsSentCount = 0;

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }

    public enum YearChangeStatus {
        ACTIVE,
        CHANGING,
        COMPLETED
    }
}
