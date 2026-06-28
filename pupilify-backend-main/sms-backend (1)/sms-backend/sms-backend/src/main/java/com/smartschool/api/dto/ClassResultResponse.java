package com.smartschool.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassResultResponse {
    private String schoolName;
    private String className;
    private String sectionName;
    private String examName;
    private String academicYear;
    private List<String> subjectNames;
    private List<StudentResultRow> studentRows;
    private int totalStudents;
    private int totalPassed;
    private int totalFailed;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StudentResultRow {
        private String enrollmentId; // 🚩 String ID
        private Integer rollNumber;  // 🚩 School Roll Number
        private String studentName;
        private List<SubjectMarks> subjectMarks;
        private int totalObtained;
        private int totalMax;
        private double percentage;
        private String grade;
        private boolean passed;
        private int rank;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectMarks {
        private String subjectName;
        private Integer theoryObtained;
        private Integer theoryMax;
        private Integer practicalObtained;
        private Integer practicalMax;
        private Integer totalObtained;
        private Integer totalMax;
        private String grade;
        private boolean passed;
        private boolean absent;
    }
}