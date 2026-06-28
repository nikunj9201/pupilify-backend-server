package com.smartschool.api.repository;

import com.smartschool.api.entity.StudentBusAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentBusAssignmentRepository extends JpaRepository<StudentBusAssignment, Long> {
    List<StudentBusAssignment> findByStudentIdAndActiveTrue(Long studentId);
    List<StudentBusAssignment> findByBusIdAndActiveTrue(Long busId);
}

