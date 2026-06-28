package com.smartschool.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {
    private Long id;
    private String subjectName;
    private String subjectCode;
    private Integer totalTheoryMarks;
    private Integer passingTheoryMarks;
    private boolean hasPractical;
    private Integer totalPracticalMarks;
    private Integer passingPracticalMarks;
    private Long schoolClassId;
    private String className; // Class ka name
    private Long sectionId;
    private String sectionName; // Section ka name
    private Long schoolId;
    private Long academicYearId;
    private boolean active; // renamed from isActive to active
}
