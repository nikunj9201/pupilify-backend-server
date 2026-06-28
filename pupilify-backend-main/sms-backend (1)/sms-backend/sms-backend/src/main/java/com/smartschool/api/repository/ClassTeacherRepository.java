package com.smartschool.api.repository;

import com.smartschool.api.entity.ClassTeacherMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassTeacherRepository extends JpaRepository<ClassTeacherMapping, Long> {

    List<ClassTeacherMapping> findBySchoolIdAndIsActiveTrue(Long schoolId);

    // 🚩 NAYA METHOD: School aur Year ID ke base par active mappings dhoondhna
    List<ClassTeacherMapping> findBySchoolIdAndAcademicYearIdAndIsActiveTrue(Long schoolId, Long academicYearId);

    List<ClassTeacherMapping> findByTeacherIdAndIsActiveTrue(Long teacherId);

    @Query("SELECT m FROM ClassTeacherMapping m WHERE m.teacher.id = :tId " +
            "AND m.schoolClass.id = :cId AND (:sId IS NULL OR m.section.id = :sId) " +
            "AND m.isActive = true")
    Optional<ClassTeacherMapping> findActiveMapping(@Param("tId") Long teacherId,
                                                    @Param("cId") Long classId,
                                                    @Param("sId") Long sectionId);

    boolean existsByTeacherIdAndAcademicYearIdAndIsActiveTrue(Long teacherId, Long yearId);
    boolean existsBySectionIdAndAcademicYearIdAndIsActiveTrue(Long sectionId, Long yearId);
    boolean existsBySchoolClassIdAndSectionIsNullAndAcademicYearIdAndIsActiveTrue(Long classId, Long yearId);
    boolean existsBySchoolIdAndSchoolClassIdAndTeacherIdAndIsActiveTrue(Long schoolId, Long classId, Long teacherId);

    @Modifying
    @Transactional
    @Query("UPDATE ClassTeacherMapping m SET m.isActive = false WHERE m.school.id = :schoolId")
    void deactivateAllBySchool(@Param("schoolId") Long schoolId);
}