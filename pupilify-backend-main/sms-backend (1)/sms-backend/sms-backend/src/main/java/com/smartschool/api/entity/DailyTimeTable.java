package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "daily_time_table")
@Data
public class DailyTimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dayOfWeek;
    private String startTime;
    private String endTime;

    // ✅ FIX: String hataya, Entity reference lagaya
    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYearConfig academicYear;

    @Column(name = "is_active", columnDefinition = "BIT(1) DEFAULT b'1'")
    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    private Section section;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
}
