package com.smartschool.api.repository;

import com.smartschool.api.entity.PrincipalDepartmentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrincipalDepartmentAssignmentRepository extends JpaRepository<PrincipalDepartmentAssignment, Long> {
    List<PrincipalDepartmentAssignment> findByPrincipalId(Long principalId);
    List<PrincipalDepartmentAssignment> findByStaff_SchoolId(Long schoolId);
}
