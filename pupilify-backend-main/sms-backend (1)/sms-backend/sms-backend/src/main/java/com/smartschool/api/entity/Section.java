package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sections")
@Data
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sectionName;

    // NAYA FEATURE: Archive check
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonIgnore
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
}