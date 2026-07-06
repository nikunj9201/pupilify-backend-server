package com.smartschool.api.service;

import com.smartschool.api.entity.Student;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface StudentService {

    // 1. Onboarding Methods
    Student onboardStudent(Student student, MultipartFile photo, MultipartFile marksheet,
                           MultipartFile tc, MultipartFile aadharImg, MultipartFile samagraImg,
                           MultipartFile passbookImg, Long schoolId, Long classId, Long sectionId) throws IOException;

    Student onboardStudentWithApaar(Student student, MultipartFile photo, MultipartFile marksheet,
                                    MultipartFile tc, MultipartFile aadharImg, MultipartFile samagraImg,
                                    MultipartFile passbookImg, MultipartFile apaarImg,
                                    MultipartFile birthCertificate, MultipartFile incomeCertificate,
                                    MultipartFile castCertificate, MultipartFile domicileCertificate,
                                    Long schoolId, Long classId, Long sectionId, Long academicYearId) throws IOException;

    // 2. Listing & Fetching Methods
    List<Student> getStudentsBySchool(Long schoolId);
    List<Student> getStudentsByClass(Long schoolId, Long classId, Long academicYearId);
    List<Student> getStudentsByClassAndSection(Long schoolId, Long classId, Long sectionId, Long academicYearId);

    Student getStudentByRollNo(String rollNo);
    Student getStudentById(Long id);
    Student getStudentByApaarId(String apaarId);

    // 3. Update Methods
    // 🚩 Purana method (Sirf JSON data ke liye)
    Student updateStudent(Long id, Student details);

    // 🚩 NAYA METHOD: Jise aapne Controller mein use kiya hai (JSON + Files)
    Student updateStudent(Long studentId, Student studentUpdateInfo,
                          MultipartFile photo, MultipartFile marksheet,
                          MultipartFile tc, MultipartFile aadharImg,
                          MultipartFile samagraImg, MultipartFile passbookImg,
                          MultipartFile apaarImg, MultipartFile birthCertificate,
                          MultipartFile incomeCertificate, MultipartFile castCertificate,
                          MultipartFile domicileCertificate) throws IOException;

    Student updateStudentByRollNo(String rollNo, Student details, Long newClassId, Long newSectionId);

    // 4. Delete & Status
    void deleteStudent(Long id);

    // 5. Statistics & Filtering
    List<Student> getStudentsByCaste(Long schoolId, String caste, Long academicYearId);
    List<Student> getStudentsByGender(Long schoolId, String gender, Long academicYearId);

    List<Map<String, Object>> getCasteStatistics(Long schoolId, Long academicYearId);
    List<Map<String, Object>> getGenderStatistics(Long schoolId, Long academicYearId);

    // 6. Promotion & Dynamic Filters
    Student promoteStudent(String rollNumber, Long classId, Long sectionId, Long academicYearId);

    List<Student> getFilteredStudents(Long schoolId, Long classId, Long sectionId, String gender, String caste, Long academicYearId);

    byte[] generateStudentReportExcel(List<Student> students) throws IOException;
}