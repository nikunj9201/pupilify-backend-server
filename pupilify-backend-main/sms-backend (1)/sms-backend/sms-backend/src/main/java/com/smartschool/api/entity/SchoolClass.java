package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "school_classes")
@Data
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    // NAYA: Archive check karne ke liye flag
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Section> sections;
}