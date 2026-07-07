package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
public class Driver {
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
    private String phoneNo;

    private String alternateNo;
    private String dob;
    private String address;
    private String aadharCardPhoto;
    private String drivingLicensePhoto;
    private String bankPassbookPhoto;

    @OneToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}