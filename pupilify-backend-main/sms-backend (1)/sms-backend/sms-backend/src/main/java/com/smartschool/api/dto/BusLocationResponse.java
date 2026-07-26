package com.smartschool.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusLocationResponse {
    private boolean isTrackingActive;
    private Double latitude;
    private Double longitude;
    private LocalDateTime lastUpdated;
    private String message;

    public static BusLocationResponse offline() {
        BusLocationResponse res = new BusLocationResponse();
        res.setTrackingActive(false);
        res.setMessage("Bus is currently offline or not tracking.");
        return res;
    }
}