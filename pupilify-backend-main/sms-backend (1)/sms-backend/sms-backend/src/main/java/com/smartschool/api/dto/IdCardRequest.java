package com.smartschool.api.dto;

import lombok.Data;

@Data
public class IdCardRequest {

    // "STUDENT" ya "TEACHER"
    private String type;

    // Student ke liye: studentId
    private Long studentId;

    // Teacher ke liye: teacherId
    private Long teacherId;

    // School ID (dono ke liye)
    private Long schoolId;

    // Academic Year (Student ke liye)
    private String academicYear;
}