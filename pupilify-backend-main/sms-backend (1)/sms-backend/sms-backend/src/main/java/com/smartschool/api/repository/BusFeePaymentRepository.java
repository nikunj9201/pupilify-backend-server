package com.smartschool.api.repository;

import com.smartschool.api.entity.BusFeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BusFeePaymentRepository extends JpaRepository<BusFeePayment, Long> {
    List<BusFeePayment> findByStudentId(Long studentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BusFeePayment p WHERE p.student.school.id = :schoolId AND p.busFeeStructure.academicYear.id = :academicYearId")
    void deleteBySchoolAndAcademicYear(@Param("schoolId") Long schoolId, @Param("academicYearId") Long academicYearId);
}
