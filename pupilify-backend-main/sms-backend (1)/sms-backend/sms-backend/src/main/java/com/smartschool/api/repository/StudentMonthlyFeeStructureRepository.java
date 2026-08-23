package com.smartschool.api.repository;

import com.smartschool.api.entity.StudentMonthlyFeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentMonthlyFeeStructureRepository extends JpaRepository<StudentMonthlyFeeStructure, Long> {
    List<StudentMonthlyFeeStructure> findByStudentIdAndAcademicYearIdAndActiveTrue(Long studentId, Long academicYearId);
    Optional<StudentMonthlyFeeStructure> findByStudentIdAndStoppageIdAndAcademicYearId(Long studentId, Long stoppageId, Long academicYearId);
    List<StudentMonthlyFeeStructure> findByBusIdAndSchoolIdAndAcademicYearIdAndActiveTrue(Long busId, Long schoolId, Long academicYearId);
    List<StudentMonthlyFeeStructure> findByStoppageIdAndAcademicYearIdAndActiveTrue(Long stoppageId, Long academicYearId);
    List<StudentMonthlyFeeStructure> findBySchoolIdAndAcademicYearIdAndActiveTrue(Long schoolId, Long academicYearId);
    List<StudentMonthlyFeeStructure> findByStudentId(Long studentId);
}

