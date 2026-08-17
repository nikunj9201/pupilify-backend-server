package com.smartschool.api.repository;

import com.smartschool.api.entity.StudentBusAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentBusAssignmentRepository extends JpaRepository<StudentBusAssignment, Long> {
    Optional<StudentBusAssignment> findByStudentIdAndAcademicYearIdAndActiveTrue(Long studentId, Long academicYearId);
    List<StudentBusAssignment> findByStudent_School_IdAndAcademicYearIdAndActiveTrue(Long schoolId, Long academicYearId);
    List<StudentBusAssignment> findByStoppage_Route_Id(Long routeId);
    List<StudentBusAssignment> findAllByStoppage_Route_Bus_Id(Long busId);
    Optional<StudentBusAssignment> findByStudentId(Long studentId);
}