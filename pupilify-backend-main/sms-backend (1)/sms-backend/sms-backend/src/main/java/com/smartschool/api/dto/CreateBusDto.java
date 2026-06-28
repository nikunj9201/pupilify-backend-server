package com.smartschool.api.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateBusDto {
    @NotBlank
    private String registrationNo;

    @Min(1)
    private Integer capacity;

    private String driverName;
}

