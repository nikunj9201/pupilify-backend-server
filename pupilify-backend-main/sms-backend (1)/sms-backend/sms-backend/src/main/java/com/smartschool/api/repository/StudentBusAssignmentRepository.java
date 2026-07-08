package com.smartschool.api.repository;

import com.smartschool.api.entity.StudentBusAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentBusAssignmentRepository extends JpaRepository<StudentBusAssignment, Long> {
    Optional<StudentBusAssignment> findByStudentIdAndAcademicYearIdAndIsActiveTrue(Long studentId, Long academicYearId);
    List<StudentBusAssignment> findByStudent_School_IdAndAcademicYearIdAndIsActiveTrue(Long schoolId, Long academicYearId);
    List<StudentBusAssignment> findByStoppage_Route_Id(Long routeId);
}