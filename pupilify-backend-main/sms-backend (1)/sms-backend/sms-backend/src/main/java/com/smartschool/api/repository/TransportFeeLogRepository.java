package com.smartschool.api.repository;

import com.smartschool.api.entity.TransportFeeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransportFeeLogRepository extends JpaRepository<TransportFeeLog, Long> {
    List<TransportFeeLog> findByStudentIdAndAcademicYearId(Long studentId, Long academicYearId);
    Optional<TransportFeeLog> findFirstByStudentIdAndAcademicYearIdAndStatusOrderByMonthYearAsc(Long studentId, Long academicYearId, TransportFeeLog.FeeStatus status);
}