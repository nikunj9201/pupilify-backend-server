package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import com.smartschool.api.entity.BusBoardingPoint;

@Entity
@Table(name = "buses")
@Data
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_no", unique = true, nullable = false)
    private String registrationNo;

    private Integer capacity;

    private String driverName;

    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BusBoardingPoint> boardingPoints;

    private LocalDateTime createdAt = LocalDateTime.now();
}
