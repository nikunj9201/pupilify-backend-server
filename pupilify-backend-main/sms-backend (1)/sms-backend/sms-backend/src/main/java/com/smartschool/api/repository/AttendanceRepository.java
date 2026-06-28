package com.smartschool.api.repository;

import com.smartschool.api.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    @Query("SELECT a FROM Attendance a " +
            "WHERE a.student.id = :studentId AND a.academicYear.id = :yearId")
    List<Attendance> findByStudentIdAndAcademicYearId(
            @Param("studentId") Long studentId,
            @Param("yearId") Long yearId);

    @Modifying @Transactional
    @Query("DELETE FROM Attendance a " +
            "WHERE a.student.id = :studentId AND a.attendanceDate = :date")
    void deleteByStudentIdAndDate(
            @Param("studentId") Long studentId,
            @Param("date") LocalDate date);

    // ✅ Teacher authorization check
    @Query("SELECT COUNT(m) > 0 FROM ClassTeacherMapping m " +
            "WHERE m.teacher.id = :tId AND m.schoolClass.id = :cId " +
            "AND (:sId IS NULL OR m.section.id = :sId) " +
            "AND m.isActive = true AND m.academicYear.id = :yearId")
    boolean isAuthorizedTeacher(
            @Param("tId") Long teacherId,
            @Param("cId") Long classId,
            @Param("sId") Long sectionId,
            @Param("yearId") Long yearId);

    @Query("SELECT a FROM Attendance a " +
            "WHERE a.school.id = :schoolId AND a.schoolClass.id = :classId " +
            "AND (:sectionId IS NULL OR a.section.id = :sectionId) " +
            "AND a.attendanceDate = :date")
    List<Attendance> findReport(
            @Param("schoolId") Long schoolId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("date") LocalDate date);

    Optional<Attendance> findByRollNumberAndAttendanceDate(
            String rollNumber, LocalDate date);

    @Query("SELECT a FROM Attendance a " +
            "WHERE a.school.id = :sId AND a.schoolClass.id = :cId " +
            "AND (:secId IS NULL OR a.section.id = :secId) " +
            "AND a.academicYear.id = :yearId")
    List<Attendance> findBySchoolClassSectionYear(
            @Param("sId") Long sId,
            @Param("cId") Long cId,
            @Param("secId") Long secId,
            @Param("yearId") Long yearId);

    @Query("SELECT a FROM Attendance a " +
            "WHERE a.school.id = :schoolId AND a.academicYear.id = :yearId")
    List<Attendance> findBySchoolIdAndAcademicYearId(
            @Param("schoolId") Long schoolId,
            @Param("yearId") Long yearId);

    @Modifying @Transactional
    @Query("DELETE FROM Attendance a " +
            "WHERE a.school.id = :schoolId AND a.academicYear.id = :yearId")
    int deleteBySchoolIdAndAcademicYearId(
            @Param("schoolId") Long schoolId,
            @Param("yearId") Long yearId);
}
