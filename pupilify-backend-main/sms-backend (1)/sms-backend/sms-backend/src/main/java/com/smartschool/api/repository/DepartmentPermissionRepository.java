package com.smartschool.api.repository;

import com.smartschool.api.entity.DepartmentPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepartmentPermissionRepository extends JpaRepository<DepartmentPermission, Long> {
    
    List<DepartmentPermission> findByDepartmentId(Long departmentId);
    
    List<DepartmentPermission> findByDepartmentIdAndIsActiveTrue(Long departmentId);
    
    boolean existsByDepartmentIdAndPermissionNameAndIsActiveTrue(Long departmentId, String permissionName);
}