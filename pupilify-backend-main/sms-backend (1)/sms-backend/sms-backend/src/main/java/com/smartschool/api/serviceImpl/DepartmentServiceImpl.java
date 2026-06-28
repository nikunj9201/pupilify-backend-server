package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.Department;
import com.smartschool.api.entity.DepartmentHead;
import com.smartschool.api.entity.DepartmentPermission;
import com.smartschool.api.entity.DepartmentType;
import com.smartschool.api.entity.User;
import com.smartschool.api.entity.Role;
import com.smartschool.api.repository.DepartmentRepository;
import com.smartschool.api.repository.DepartmentHeadRepository;
import com.smartschool.api.repository.DepartmentPermissionRepository;
import com.smartschool.api.repository.UserRepository;
import com.smartschool.api.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DepartmentHeadRepository departmentHeadRepository;
    @Autowired
    private DepartmentPermissionRepository departmentPermissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Department createDepartment(Department department) {
        Department savedDept = departmentRepository.save(department);
        // Use provided username and password
        String username = department.getUsername();
        String rawPassword = department.getPassword();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        User deptUser = new User();
        deptUser.setUsername(username);
        deptUser.setPassword(passwordEncoder.encode(rawPassword));
        deptUser.setRole(Role.ROLE_DEPARTMENT); // Changed from ROLE_ADMIN to ROLE_DEPARTMENT
        deptUser.setSchool(savedDept.getSchool());
        deptUser.setDepartment(savedDept.getDeptName());
        deptUser.setDepartmentId(savedDept.getId());
        deptUser.setActive(true);
        userRepository.save(deptUser);
        return savedDept;
    }

    @Override
    public List<Department> getDepartmentsBySchool(Long schoolId) {
        return departmentRepository.findBySchoolId(schoolId);
    }

    @Override
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    @Override
    public Optional<Department> getDepartmentByType(Long schoolId, DepartmentType deptType) {
        return departmentRepository.findBySchoolIdAndDeptType(schoolId, deptType);
    }

    @Override
    public Department updateDepartment(Long id, Department departmentDetails) {
        Optional<Department> opt = departmentRepository.findById(id);
        if (opt.isPresent()) {
            Department dept = opt.get();
            dept.setDeptName(departmentDetails.getDeptName());
            dept.setDeptType(departmentDetails.getDeptType());
            dept.setSchool(departmentDetails.getSchool());
            return departmentRepository.save(dept);
        }
        throw new RuntimeException("Department not found");
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    @Override
    public DepartmentHead assignDepartmentHead(DepartmentHead departmentHead) {
        return departmentHeadRepository.save(departmentHead);
    }

    @Override
    public Optional<DepartmentHead> getDepartmentHead(Long deptId) {
        return departmentHeadRepository.findByDepartmentId(deptId);
    }

    @Override
    public void removeDepartmentHead(Long deptId) {
        // No direct deleteByDepartmentId, so delete by finding the entity first
        departmentHeadRepository.findByDepartmentId(deptId)
            .ifPresent(departmentHeadRepository::delete);
    }

    @Override
    public DepartmentPermission addPermission(DepartmentPermission permission) {
        return departmentPermissionRepository.save(permission);
    }

    @Override
    public List<DepartmentPermission> getPermissions(Long deptId) {
        return departmentPermissionRepository.findByDepartmentId(deptId);
    }

    @Override
    public List<DepartmentPermission> getActivePermissions(Long deptId) {
        return departmentPermissionRepository.findByDepartmentIdAndIsActiveTrue(deptId);
    }

    @Override
    public DepartmentPermission updatePermission(Long permId, DepartmentPermission permissionDetails) {
        Optional<DepartmentPermission> opt = departmentPermissionRepository.findById(permId);
        if (opt.isPresent()) {
            DepartmentPermission perm = opt.get();
            perm.setPermissionName(permissionDetails.getPermissionName());
            perm.setIsActive(permissionDetails.getIsActive());
            return departmentPermissionRepository.save(perm);
        }
        throw new RuntimeException("Permission not found");
    }

    @Override
    public void removePermission(Long permId) {
        departmentPermissionRepository.deleteById(permId);
    }
}
