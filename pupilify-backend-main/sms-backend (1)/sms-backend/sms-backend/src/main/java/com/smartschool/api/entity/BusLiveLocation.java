package com.smartschool.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus_live_locations")
@Data
public class BusLiveLocation {

    @Id
    private Long busId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "bus_id")
    @JsonIgnore
    private Bus bus;

    private double latitude;
    private double longitude;
    private double speed;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isTrackingActive = false;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}