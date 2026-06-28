package com.smartschool.api.service;

import com.smartschool.api.entity.Department;
import com.smartschool.api.entity.DepartmentHead;
import com.smartschool.api.entity.DepartmentPermission;
import com.smartschool.api.entity.DepartmentType;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    // ==================== DEPARTMENT MANAGEMENT ====================
    Department createDepartment(Department department);

    List<Department> getDepartmentsBySchool(Long schoolId);

    Optional<Department> getDepartmentById(Long id);

    Optional<Department> getDepartmentByType(Long schoolId, DepartmentType deptType);

    Department updateDepartment(Long id, Department departmentDetails);

    void deleteDepartment(Long id);

    // ==================== DEPARTMENT HEAD MANAGEMENT ====================
    DepartmentHead assignDepartmentHead(DepartmentHead departmentHead);

    Optional<DepartmentHead> getDepartmentHead(Long deptId);

    void removeDepartmentHead(Long deptId);

    // ==================== PERMISSION MANAGEMENT ====================
    DepartmentPermission addPermission(DepartmentPermission permission);

    List<DepartmentPermission> getPermissions(Long deptId);

    List<DepartmentPermission> getActivePermissions(Long deptId);

    DepartmentPermission updatePermission(Long permId, DepartmentPermission permissionDetails);

    void removePermission(Long permId);
}

