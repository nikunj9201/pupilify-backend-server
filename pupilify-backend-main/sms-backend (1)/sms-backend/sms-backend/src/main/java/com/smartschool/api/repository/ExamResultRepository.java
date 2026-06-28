package com.smartschool.api.repository;

import com.smartschool.api.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    // ─────────────────────────────────────────────────────────
    // 1. STUDENT KA RESULT — ek exam ke liye saare subjects
    // ─────────────────────────────────────────────────────────

    /**
     * Student ka ek specific exam (e.g. "Half Yearly") ka poora result.
     * Result card ke liye use karo.
     */
    @Query("SELECT r FROM ExamResult r " +
            "JOIN FETCH r.subject s " +
            "WHERE r.student.id = :studentId " +
            "AND r.examSchedule.examName = :examName " +
            "AND r.academicYear.id = :yearId " +
            "ORDER BY s.subjectName ASC")
    List<ExamResult> findStudentResultByExam(
            @Param("studentId") Long studentId,
            @Param("examName")  String examName,
            @Param("yearId")    Long yearId);

    /**
     * Student ke saare exams ke saare results (poore saal ke).
     * Student progress report ke liye use karo.
     */
    @Query("SELECT r FROM ExamResult r " +
            "JOIN FETCH r.subject s " +
            "JOIN FETCH r.examSchedule e " +
            "WHERE r.student.id = :studentId " +
            "AND r.academicYear.id = :yearId " +
            "ORDER BY e.examDate ASC, s.subjectName ASC")
    List<ExamResult> findAllResultsByStudent(
            @Param("studentId") Long studentId,
            @Param("yearId")    Long yearId);

    // ─────────────────────────────────────────────────────────
    // 2. CLASS RESULT SHEET
    // ─────────────────────────────────────────────────────────

    /**
     * Ek class (+ optional section) ka ek exam ka poora result.
     * sectionId = null → poori class ke results.
     * Excel export aur class result sheet ke liye use karo.
     */
    @Query("SELECT r FROM ExamResult r " +
            "JOIN FETCH r.student st " +
            "JOIN FETCH r.subject su " +
            "WHERE r.school.id = :schoolId " +
            "AND r.examSchedule.schoolClass.id = :classId " +
            "AND (:sectionId IS NULL " +
            "     OR r.examSchedule.section.id = :sectionId) " +
            "AND r.examSchedule.examName = :examName " +
            "AND r.academicYear.id = :yearId " +
            "ORDER BY st.rollNumber ASC, su.subjectName ASC")
    List<ExamResult> findClassResults(
            @Param("schoolId")  Long schoolId,
            @Param("classId")   Long classId,
            @Param("sectionId") Long sectionId,
            @Param("examName")  String examName,
            @Param("yearId")    Long yearId);

    // ─────────────────────────────────────────────────────────
    // 3. EXAM SCHEDULE KE LIYE SAARE RESULTS
    // ─────────────────────────────────────────────────────────

    /**
     * Ek specific ExamSchedule ke existing results.
     * Marks upload se pehle check karne ke liye use karo.
     */
    List<ExamResult> findByExamScheduleId(Long examScheduleId);

    // ─────────────────────────────────────────────────────────
    // 4. DROPDOWN — unique exam names
    // ─────────────────────────────────────────────────────────

    /**
     * School mein jo bhi exams hua hain unke unique names.
     * Frontend dropdown ke liye use karo.
     */
    @Query("SELECT DISTINCT r.examSchedule.examName " +
            "FROM ExamResult r " +
            "WHERE r.school.id = :schoolId " +
            "AND r.academicYear.id = :yearId " +
            "ORDER BY r.examSchedule.examName ASC")
    List<String> findUniqueExamNames(
            @Param("schoolId") Long schoolId,
            @Param("yearId")   Long yearId);

    // ─────────────────────────────────────────────────────────
    // 5. OVERWRITE LOGIC — pehle purana delete karo
    // ─────────────────────────────────────────────────────────

    /**
     * Same student ka same exam ka purana result delete karo.
     * uploadBulkResults se pehle call karo (overwrite support).
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ExamResult r " +
            "WHERE r.student.id = :studentId " +
            "AND r.examSchedule.id = :examScheduleId")
    void deleteByStudentIdAndExamScheduleId(
            @Param("studentId")      Long studentId,
            @Param("examScheduleId") Long examScheduleId);

    // ─────────────────────────────────────────────────────────
    // 6. ACADEMIC YEAR CHANGE — data export ke liye
    // ─────────────────────────────────────────────────────────

    /**
     * School ka saara result data ek academic year ke liye.
     * Year change pe Excel export ke baad ye sab delete hoga.
     */
    @Query("SELECT r FROM ExamResult r " +
            "WHERE r.school.id = :schoolId " +
            "AND r.academicYear.id = :yearId")
    List<ExamResult> findBySchoolIdAndAcademicYearId(
            @Param("schoolId") Long schoolId,
            @Param("yearId")   Long yearId);

    // ─────────────────────────────────────────────────────────
    // 7. NEW LOGIC — Find results directly by Roll Number (Added)
    // ─────────────────────────────────────────────────────────

    /**
     * Roll Number ke base par students ke results dhoondne ke liye query.
     */
    @Query("SELECT r FROM ExamResult r " +
            "JOIN FETCH r.subject s " +
            "WHERE r.student.rollNumber = :rollNumber " +
            "AND r.school.id = :schoolId " +
            "AND r.examSchedule.examName = :examName " +
            "AND r.academicYear.id = :yearId " +
            "ORDER BY s.subjectName ASC")
    List<ExamResult> findByStudentRollNumberAndExam(
            @Param("rollNumber") String rollNumber,
            @Param("schoolId")   Long schoolId,
            @Param("examName")   String examName,
            @Param("yearId")     Long yearId);

    // ─────────────────────────────────────────────────────────
    // 8. SUBJECT REPOSITORY CHECK
    // ─────────────────────────────────────────────────────────

    /**
     * Duplicate check — same student + same subject + same exam
     */
    boolean existsByStudentIdAndSubjectIdAndExamScheduleId(
            Long studentId,
            Long subjectId,
            Long examScheduleId);
}