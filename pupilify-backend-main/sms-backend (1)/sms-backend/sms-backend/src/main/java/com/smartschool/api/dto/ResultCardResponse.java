package com.smartschool.api.dto;

import com.smartschool.api.entity.ExamResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultCardResponse {
    private String studentName;
    private String enrollmentId; // 🚩 Unique String ID
    private Integer rollNumber;   // 🚩 School Roll Number (Integer)
    private String className;
    private String sectionName;
    private String examName;
    private String academicYear;

    private List<ExamResult> subjectResults;

    private Integer totalObtained;
    private Integer totalMax;
    private Double percentage;
    private String grade;
    private boolean passed;

    private long subjectsPassed;
    private long subjectsFailed;
    private long subjectsAbsent;
}