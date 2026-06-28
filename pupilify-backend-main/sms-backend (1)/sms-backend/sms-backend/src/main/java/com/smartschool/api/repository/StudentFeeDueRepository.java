package com.smartschool.api.repository;

import com.smartschool.api.entity.StudentFeeDue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeeDueRepository extends JpaRepository<StudentFeeDue, Long> {

    // 1. Ek student ki saari PENDING dues (cleared = false) - Via Numeric ID
    List<StudentFeeDue> findByStudentIdAndClearedFalse(Long studentId);

    // 2. School ki saari pending dues — principal dashboard
    List<StudentFeeDue> findBySchoolIdAndClearedFalse(Long schoolId);

    // 3. Ek student ki saari dues — cleared + pending dono - Via Numeric ID
    List<StudentFeeDue> findByStudentId(Long studentId);

    // 4. Duplicate prevention logic
    Optional<StudentFeeDue> findByStudentIdAndFromAcademicYearId(
            Long studentId, Long fromAcademicYearId);

    // ─────────────────────────────────────────────────────────
    // 🚩 NAYE METHODS: ENROLLMENT ID (Unique String) SE DUES FETCH KARNE KE LIYE
    // ─────────────────────────────────────────────────────────

    /**
     * Enrollment ID se uncleared dues nikalne ke liye.
     * Isse Academic Year badalne par bhi dues gayab nahi hongi.
     */
    @Query("SELECT d FROM StudentFeeDue d WHERE d.student.enrollmentId = :enrollmentId AND d.cleared = false")
    List<StudentFeeDue> findByEnrollmentIdAndClearedFalse(@Param("enrollmentId") String enrollmentId);

    /**
     * Ek student ki poori dues history Enrollment ID se dekhne ke liye.
     */
    @Query("SELECT d FROM StudentFeeDue d WHERE d.student.enrollmentId = :enrollmentId")
    List<StudentFeeDue> findByEnrollmentId(@Param("enrollmentId") String enrollmentId);

    /**
     * School wise uncleared dues search by Enrollment ID
     */
    @Query("SELECT d FROM StudentFeeDue d WHERE d.school.id = :schoolId AND d.student.enrollmentId = :enrollmentId AND d.cleared = false")
    List<StudentFeeDue> findBySchoolAndEnrollmentId(@Param("schoolId") Long schoolId, @Param("enrollmentId") String enrollmentId);
}