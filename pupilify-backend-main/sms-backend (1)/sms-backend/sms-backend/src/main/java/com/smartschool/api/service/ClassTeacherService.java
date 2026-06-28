package com.smartschool.api.service;

import com.smartschool.api.entity.ClassTeacherMapping;
import java.util.List;

public interface ClassTeacherService {

    // 1. Existing: Assign Teacher (Academic Year ID ke saath)
    ClassTeacherMapping assignClassTeacher(Long classId, Long sectionId, Long teacherId, Long schoolId, Long academicYearId);

    // 2. Existing: School ki saari mappings ke liye
    List<ClassTeacherMapping> getMappingsBySchool(Long schoolId);

    // 3. Existing: Specific Year ki mappings dekhne ke liye
    List<ClassTeacherMapping> getMappingsBySchoolAndYear(Long schoolId, Long academicYearId);

    // 4. Existing: Delete Mapping
    void deleteMapping(Long id);

    // 🚩 NAYA FEATURE: Teacher ki apni classes load karne ke liye (Dashboard Fix)
    // Iske bina Controller mein 'Method Not Found' ki error aayegi
    List<ClassTeacherMapping> getMappingsByTeacher(Long teacherId);
}