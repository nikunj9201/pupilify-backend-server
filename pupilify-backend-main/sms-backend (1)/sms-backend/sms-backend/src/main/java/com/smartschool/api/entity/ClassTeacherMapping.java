package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "class_teacher_mappings")
@Data
public class ClassTeacherMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Naya Entity-based reference (Ise rehne den)
    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYearConfig academicYear;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    private Section section;

    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    // ❌ Purana 'private String academicYear;' yahan se hata diya gaya hai
}