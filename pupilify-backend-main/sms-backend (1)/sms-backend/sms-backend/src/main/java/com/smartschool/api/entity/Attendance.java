package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Data
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate attendanceDate;
    private String rollNumber;
    private String status; // Present, Absent, etc.

    // 🚩 ISSE UPDATE KAREIN: String ki jagah AcademicYearConfig use karein
    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYearConfig academicYear;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "school_class_id")
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    private Section section;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
}