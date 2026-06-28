package com.smartschool.api.repository;

import com.smartschool.api.entity.Student;
import com.smartschool.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // 1. Basic Fetch Methods
    List<Student> findBySchoolId(Long schoolId);
    List<Student> findBySchoolIdAndIsActiveTrue(Long schoolId);

    // 2. Class & Section Fetch with Academic Year ID
    List<Student> findBySchoolIdAndSchoolClassIdAndAcademicYearIdAndIsActiveTrue(Long schoolId, Long classId, Long yearId);
    List<Student> findBySchoolIdAndSchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(Long schoolId, Long classId, Long sectionId, Long yearId);

    // 🚩 Naya: Roll Number generation ke liye Alphabetical Sort (A to Z)
    List<Student> findBySchoolIdAndSchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrueOrderByNameAsc(Long schoolId, Long classId, Long sectionId, Long yearId);

    // 3. Purane Compatibility Methods
    List<Student> findBySchoolIdAndSchoolClassId(Long schoolId, Long classId);
    List<Student> findBySchoolIdAndSchoolClassIdAndSectionId(Long schoolId, Long classId, Long sectionId);

    // 4. Enrollment ID (Unique String ID - Search ke liye)
    Optional<Student> findByEnrollmentIdAndIsActiveTrue(String enrollmentId);

    @Query("SELECT MAX(s.enrollmentId) FROM Student s")
    String findLastEnrollmentId();

    // 5. Roll Number (Integer - Class wise 1, 2, 3...)
    // 🚩 String ko Integer mein badla taaki error na aaye
    Optional<Student> findByRollNumberAndIsActiveTrue(Integer rollNumber);
    boolean existsByRollNumber(Integer rollNumber);

    @Query("SELECT s.rollNumber FROM Student s ORDER BY s.id DESC LIMIT 1")
    Integer findLastRollNumber();

    @Query("SELECT s FROM Student s WHERE s.rollNumber = :rollNumber AND s.school.id = :schoolId AND s.isActive = true")
    Optional<Student> findByRollNumberAndSchoolId(@Param("rollNumber") Integer rollNumber, @Param("schoolId") Long schoolId);

    // 6. User & Email Validation
    Optional<Student> findByUser(User user);
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.isActive = false WHERE s.school.id = :schoolId")
    void deactivateStudentsBySchoolId(Long schoolId);

    // 7. Section/Class Validation
    boolean existsBySchoolClassIdAndIsActiveTrue(Long classId);
    boolean existsBySectionIdAndIsActiveTrue(Long sectionId);

    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId " +
            "AND s.schoolClass.id = :classId " +
            "AND (:sectionId IS NULL OR s.section.id = :sectionId) " +
            "AND s.isActive = true")
    List<Student> findActiveStudentsForTeacher(
            @Param("schoolId") Long schoolId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId);

    // 8. Caste & Gender Statistics (Year Wise)
    List<Student> findBySchoolIdAndCasteAndAcademicYearIdAndIsActiveTrue(Long schoolId, String caste, Long yearId);

    @Query("SELECT s.caste as caste, COUNT(s) as count FROM Student s " +
            "WHERE s.school.id = :schoolId AND s.academicYear.id = :yearId AND s.isActive = true GROUP BY s.caste")
    List<Map<String, Object>> getCasteWiseCount(@Param("schoolId") Long schoolId, @Param("yearId") Long yearId);

    List<Student> findBySchoolIdAndGenderAndAcademicYearIdAndIsActiveTrue(Long schoolId, String gender, Long yearId);

    @Query("SELECT s.gender as gender, COUNT(s) as count FROM Student s " +
            "WHERE s.school.id = :schoolId AND s.academicYear.id = :yearId AND s.isActive = true GROUP BY s.gender")
    List<Map<String, Object>> getGenderWiseCount(@Param("schoolId") Long schoolId, @Param("yearId") Long yearId);

    // 9. APAAR ID Search
    Optional<Student> findByApaarIdAndIsActiveTrue(String apaarId);
    boolean existsByApaarId(String apaarId);

    // 10. ADVANCED FILTER METHOD
    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId " +
            "AND (:classId IS NULL OR s.schoolClass.id = :classId) " +
            "AND (:sectionId IS NULL OR s.section.id = :sectionId) " +
            "AND (:gender IS NULL OR s.gender = :gender) " +
            "AND (:caste IS NULL OR s.caste = :caste) " +
            "AND s.academicYear.id = :academicYearId " +
            "AND s.isActive = true")
    List<Student> findByFilters(
            @Param("schoolId") Long schoolId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("gender") String gender,
            @Param("caste") String caste,
            @Param("academicYearId") Long academicYearId
    );

    // 🚩 String parameters ko Integer mein badla (Class ID compatibility ke liye)
    Optional<Student> findByRollNumberAndSchoolIdAndSchoolClassId(
            Integer rollNumber,
            Long schoolId,
            Long schoolClassId
    );

    Optional<Student> findByRollNumberAndSchoolIdAndSchoolClassIdAndSectionId(
            Integer rollNumber,
            Long schoolId,
            Long schoolClassId,
            Long sectionId
    );
}