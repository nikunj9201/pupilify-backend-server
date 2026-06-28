package com.smartschool.api.repository;

import com.smartschool.api.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // ✅ Year-wise fetch logic [cite: 161]
    List<Subject> findBySchoolIdAndIsActiveTrueAndAcademicYearId(Long schoolId, Long yearId);

    List<Subject> findBySchoolIdAndIsActiveTrue(Long schoolId);

    List<Subject> findBySchoolIdAndSchoolClassIdAndIsActiveTrueAndAcademicYearId(Long schoolId, Long classId, Long yearId);

    // ✅ Custom query for filtering [cite: 163]
    @Query("SELECT s FROM Subject s WHERE s.school.id = :schoolId " +
            "AND s.schoolClass.id = :classId " +
            "AND (:sectionId IS NULL OR s.section.id = :sectionId) " +
            "AND s.isActive = true AND s.academicYear.id = :yearId")
    List<Subject> findActiveSubjects(@Param("schoolId") Long schoolId,
                                     @Param("classId") Long classId,
                                     @Param("sectionId") Long sectionId,
                                     @Param("yearId") Long academicYearId);


    boolean existsBySubjectCodeAndSchoolIdAndAcademicYearIdAndIsActiveTrue(String code, Long schoolId, Long yearId);

    // ✅ Soft Deactivation Logic [cite: 166, 168, 169]
    @Modifying
    @Transactional
    @Query("UPDATE Subject s SET s.isActive = false WHERE s.schoolClass.id = :classId AND s.section.id = :sectionId AND s.school.id = :schoolId")
    void deactivateSubjectsBySection(@Param("classId") Long classId, @Param("sectionId") Long sectionId, @Param("schoolId") Long schoolId);

    @Modifying
    @Transactional
    @Query("UPDATE Subject s SET s.isActive = false WHERE s.schoolClass.id = :classId AND s.school.id = :schoolId")
    void deactivateSubjectsByClass(@Param("classId") Long classId, @Param("schoolId") Long schoolId);

    @Modifying
    @Transactional
    @Query("UPDATE Subject s SET s.isActive = false WHERE s.school.id = :schoolId")
    void deactivateAllBySchool(@Param("schoolId") Long schoolId);

    // SubjectRepository.java
    List<Subject> findBySchoolIdAndAcademicYearIdAndIsActiveTrue(Long schoolId, Long academicYearId);


}