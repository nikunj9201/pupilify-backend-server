package com.smartschool.api.repository;

import com.smartschool.api.entity.Department;
import com.smartschool.api.entity.DepartmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findBySchoolId(Long schoolId);
    Optional<Department> findBySchoolIdAndDeptType(Long schoolId, DepartmentType deptType);
    List<Department> findByDeptType(DepartmentType deptType);
    Optional<Department> findByDeptNameAndSchoolId(String deptName, Long schoolId);
}

