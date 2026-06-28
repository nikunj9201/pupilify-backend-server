package com.smartschool.api.repository;

import com.smartschool.api.entity.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {

    /**
     * Class (+ optional section) ke saare active exam schedules.
     * Exam timetable view ke liye.
     * ✅ EAGERLY FETCH all nested relationships
     */
    @Query("SELECT DISTINCT e FROM ExamSchedule e " +
            "JOIN FETCH e.subject s " +
            "JOIN FETCH e.schoolClass sc " +
            "JOIN FETCH e.school sch " +
            "LEFT JOIN FETCH sch.currentYear " +
            "LEFT JOIN FETCH e.section " +
            "WHERE e.school.id = :schoolId " +
            "AND e.schoolClass.id = :classId " +
            "AND (:sectionId IS NULL OR e.section.id = :sectionId) " +
            "AND e.academicYear.id = :yearId " +
            "AND e.isActive = true " +
            "ORDER BY e.examDate ASC, s.subjectName ASC")
    List<ExamSchedule> findBySchoolClassSection(
            @Param("schoolId")  Long schoolId,
            @Param("classId")   Long classId,
            @Param("sectionId") Long sectionId,
            @Param("yearId")    Long yearId);

    /**
     * Ek specific examName ke saare schedules (saare subjects).
     * e.g. "Half Yearly" ke saare subjects ka schedule.
     * ✅ EAGERLY FETCH all nested relationships
     */
    @Query("SELECT DISTINCT e FROM ExamSchedule e " +
            "JOIN FETCH e.subject s " +
            "JOIN FETCH e.schoolClass sc " +
            "JOIN FETCH e.school sch " +
            "LEFT JOIN FETCH sch.currentYear " +
            "LEFT JOIN FETCH e.section " +
            "WHERE e.school.id = :schoolId " +
            "AND e.examName = :examName " +
            "AND e.academicYear.id = :yearId " +
            "AND e.isActive = true " +
            "ORDER BY e.examDate ASC")
    List<ExamSchedule> findByExamName(
            @Param("schoolId") Long schoolId,
            @Param("examName") String examName,
            @Param("yearId")   Long yearId);

    /**
     * School ke all unique exam names (dropdown ke liye).
     */
    @Query("SELECT DISTINCT e.examName FROM ExamSchedule e " +
            "WHERE e.school.id = :schoolId " +
            "AND e.academicYear.id = :yearId " +
            "AND e.isActive = true " +
            "ORDER BY e.examName ASC")
    List<String> findUniqueExamNames(
            @Param("schoolId") Long schoolId,
            @Param("yearId")   Long yearId);

    /**
     * Class (+ optional section) ke saare active exam schedules, with optional examName filter.
     * Exam timetable view ke liye.
     * ✅ EAGERLY FETCH all nested relationships to prevent Hibernate lazy loading errors
     */
    @Query("SELECT DISTINCT e FROM ExamSchedule e " +
            "JOIN FETCH e.subject s " +
            "JOIN FETCH e.schoolClass sc " +
            "JOIN FETCH e.school sch " +
            "LEFT JOIN FETCH sch.currentYear " +
            "LEFT JOIN FETCH e.section " +
            "WHERE e.school.id = :schoolId " +
            "AND e.schoolClass.id = :classId " +
            "AND (:sectionId IS NULL OR e.section.id = :sectionId) " +
            "AND e.academicYear.id = :yearId " +
            "AND (:examName IS NULL OR e.examName = :examName) " +
            "AND e.isActive = true " +
            "ORDER BY e.examDate ASC, s.subjectName ASC")
    List<ExamSchedule> findFilteredSchedule(
            @Param("schoolId")  Long schoolId,
            @Param("classId")   Long classId,
            @Param("sectionId") Long sectionId,
            @Param("yearId")    Long yearId,
            @Param("examName")  String examName);
}