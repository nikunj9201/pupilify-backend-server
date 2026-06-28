package com.smartschool.api.service;

import com.smartschool.api.entity.Teacher;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import com.smartschool.api.entity.ClassTeacherMapping;
public interface TeacherService {
    Teacher onboardTeacher(Teacher teacher, MultipartFile photo, MultipartFile aadharImg, MultipartFile passbookImg, Long schoolId) throws IOException;
    List<Teacher> getTeachersBySchool(Long schoolId);
    Teacher getTeacherById(Long id);
    Teacher updateTeacher(Long id, Teacher details);
    void deleteTeacher(Long id);
    List<ClassTeacherMapping> getActiveMappingsForTeacher(Long teacherId);

    byte[] getTeacherDocument(Long id, String docType);
}