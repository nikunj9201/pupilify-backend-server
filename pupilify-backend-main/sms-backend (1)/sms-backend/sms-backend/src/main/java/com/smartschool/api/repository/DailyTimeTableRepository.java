package com.smartschool.api.repository;

import com.smartschool.api.entity.DailyTimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface DailyTimeTableRepository
        extends JpaRepository<DailyTimeTable, Long> {

    List<DailyTimeTable> findByTeacherIdAndIsActiveTrue(Long teacherId);

    // ✅ FIX: academicYear.id se compare karo (String se nahi)
    @Query("SELECT t FROM DailyTimeTable t WHERE t.school.id = :schoolId " +
            "AND t.schoolClass.id = :classId " +
            "AND (:sectionId IS NULL OR t.section.id = :sectionId) " +
            "AND t.isActive = true AND t.academicYear.id = :yearId")
    List<DailyTimeTable> findActiveSchedule(
            @Param("schoolId") Long schoolId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("yearId") Long yearId);

    // ✅ FIX: Teacher conflict check — entity ID se
    @Query("SELECT COUNT(t) > 0 FROM DailyTimeTable t " +
            "WHERE t.teacher.id = :tId AND t.dayOfWeek = :day " +
            "AND t.startTime = :start AND t.academicYear.id = :yearId " +
            "AND t.isActive = true")
    boolean isTeacherBusy(
            @Param("tId") Long teacherId,
            @Param("day") String day,
            @Param("start") String start,
            @Param("yearId") Long yearId);

    // ✅ FIX: Teacher schedule — entity ID se
    @Query("SELECT t FROM DailyTimeTable t " +
            "WHERE t.teacher.id = :tId AND t.dayOfWeek = :day " +
            "AND t.academicYear.id = :yearId AND t.isActive = true " +
            "ORDER BY t.startTime ASC")
    List<DailyTimeTable> findTeacherDaySchedule(
            @Param("tId") Long teacherId,
            @Param("day") String day,
            @Param("yearId") Long yearId);

    @Modifying @Transactional
    @Query("UPDATE DailyTimeTable t SET t.isActive = false" +
            " WHERE t.schoolClass.id = :classId AND t.school.id = :schoolId")
    void deactivateByClass(
            @Param("classId") Long classId,
            @Param("schoolId") Long schoolId);

    @Modifying @Transactional
    @Query("UPDATE DailyTimeTable t SET t.isActive = false" +
            " WHERE t.section.id = :sectionId AND t.school.id = :schoolId")
    void deactivateBySection(
            @Param("sectionId") Long sectionId,
            @Param("schoolId") Long schoolId);

    @Modifying @Transactional
    @Query("UPDATE DailyTimeTable t SET t.isActive = false" +
            " WHERE t.school.id = :schoolId")
    void deactivateAllBySchool(@Param("schoolId") Long schoolId);
}
