package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "teachers")
@Data // ✅ Lombok automatically creates isActive() and setActive()
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Personal & Contact
    private String name;
    private String address;
    private String mail;
    private String password;
    private String dob;
    private String phoneNumber;
    private String alternateNumber;

    // Professional & Financial
    private String qualification;
    private String subjectExpertise;
    private Double salary;
    private String aadharNo;

    // Image Paths
    private String aadharImage;
    private String teacherPhoto;
    private String bankPassbookImage;

    // ✅ Naya Field: Soft Delete aur Status check ke liye
    @Column(name = "is_active", columnDefinition = "BIT(1) DEFAULT b'1'")
    private boolean active = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    private LocalDateTime createdAt = LocalDateTime.now();
}