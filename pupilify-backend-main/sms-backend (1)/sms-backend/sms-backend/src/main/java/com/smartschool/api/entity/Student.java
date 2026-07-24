package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🚩 Renamed from rollNumber: Ye system-wide unique Enrollment ID hai
    @Column(unique = true)
    private String enrollmentId;

    // 🚩 Naya field: Ye class/section wise roll number (1, 2, 3...) hai
    private Integer rollNumber;

    private String name;

    // 🚩 NAYA OPTION: Gender (Male/Female/Other)
    private String gender;

    private String phoneNo;
    private String email;
    private String password;
    private String address;
    private String dob;

    private String fatherName;
    private String motherName;
    private String fatherContactNumber;

    // 🚩 Caste (SC/ST/OBC/General)
    private String caste;

    // 🚩 APAAR ID (12-digit unique academic ID)
    @Column(unique = true)
    private String apaarId;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    private Section section;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYearConfig academicYear;

    private String aadharCardNo;
    private String samagraId;

    private String lastClassMarksheet;
    private String tcImage;
    private String aadharCardImage;
    private String samagraIdImage;
    private String bankPassbookImage;
    private String studentPhoto;
    private String apaarCardImage;

    private String familyId;
    private String scholarNo;
    private String fatherOccupation;
    private String fatherSalary;
    private String postalCode;
    private String bankName;
    private String bankAccountNo;
    private String ifscCode;
    private String branch;
    private String birthCertificate;
    private String incomeCertificate;
    private String castCertificate;
    private String domicileCertificate;
    private String penNumber;


    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isActive = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.isActive = true;
        if(this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}