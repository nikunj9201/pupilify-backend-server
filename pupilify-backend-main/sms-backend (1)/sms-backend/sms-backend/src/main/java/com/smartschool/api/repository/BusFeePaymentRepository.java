package com.smartschool.api.repository;

import com.smartschool.api.entity.BusFeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusFeePaymentRepository extends JpaRepository<BusFeePayment, Long> {
    List<BusFeePayment> findByStudentIdAndAcademicYearId(Long studentId, Long academicYearId);
}