package com.smartschool.api.dto;

import lombok.Data;

@Data
public class BusAssignmentDTO {
    private Long assignmentId;
    private Long studentId;
    private String enrollmentId;
    private String name;
    private String phoneNo;
    private String stopName;
    private double fee;
}