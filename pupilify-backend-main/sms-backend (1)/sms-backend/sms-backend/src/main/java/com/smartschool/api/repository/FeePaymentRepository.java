package com.smartschool.api.repository;

import com.smartschool.api.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

    List<FeePayment> findByStudentIdOrderByPaymentDateDesc(Long studentId);

    // 🚩 FIX: findBySchoolIdAndAcademicYearId (Entity mapping ke hisaab se)
    List<FeePayment> findBySchoolIdAndAcademicYearId(Long schoolId, Long yearId);

    @Modifying
    @Transactional
    @Query("DELETE FROM FeePayment f WHERE f.school.id = :schoolId AND f.academicYear.id = :yearId")
    int deleteBySchoolIdAndAcademicYearId(@Param("schoolId") Long schoolId, @Param("yearId") Long yearId);

    @Query("SELECT SUM(f.amountPaid) FROM FeePayment f WHERE f.school.id = :schoolId AND f.academicYear.id = :yearId")
    Double getTotalFeesCollectedBySchool(@Param("schoolId") Long schoolId, @Param("yearId") Long yearId);

    List<FeePayment> findByStudentIdAndAcademicYearId(Long studentId, Long academicYearId);
}   