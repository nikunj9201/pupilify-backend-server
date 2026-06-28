package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "schools")
@Data
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String schoolName;

    @Column(unique = true, nullable = false)
    private String mailId;

    private String address;
    private String phoneNumber;

    // 🚩 NAYA: School Logo ka file name store karne ke liye
    private String schoolLogo;

    @Transient
    private String password;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.TRIAL;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "current_academic_year_id")
    private AcademicYearConfig currentYear;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}