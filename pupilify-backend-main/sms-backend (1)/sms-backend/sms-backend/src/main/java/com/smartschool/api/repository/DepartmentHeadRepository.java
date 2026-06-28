package com.smartschool.api.repository;

import com.smartschool.api.entity.DepartmentHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentHeadRepository extends JpaRepository<DepartmentHead, Long> {
    Optional<DepartmentHead> findByDepartmentId(Long departmentId);
    Optional<DepartmentHead> findByUserId(Long userId);
    boolean existsByDepartmentId(Long departmentId);
}

