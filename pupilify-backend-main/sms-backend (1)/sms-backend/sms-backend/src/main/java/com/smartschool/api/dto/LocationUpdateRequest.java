package com.smartschool.api.dto;

import lombok.Data;

@Data
public class LocationUpdateRequest {
    private double latitude;
    private double longitude;
    private double speed;
}