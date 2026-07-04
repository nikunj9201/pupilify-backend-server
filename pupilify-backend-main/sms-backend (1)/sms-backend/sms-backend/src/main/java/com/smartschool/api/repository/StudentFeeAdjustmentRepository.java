package com.smartschool.api.repository;

import com.smartschool.api.entity.StudentFeeAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentFeeAdjustmentRepository extends JpaRepository<StudentFeeAdjustment, Long> {
    List<StudentFeeAdjustment> findByStudentId(Long studentId);
    List<StudentFeeAdjustment> findByStudentIdAndAcademicYearIdAndIsArchivedFalse(Long studentId, Long academicYearId);
    List<StudentFeeAdjustment> findByStudentIdAndAcademicYearId(Long studentId, Long academicYearId);
}