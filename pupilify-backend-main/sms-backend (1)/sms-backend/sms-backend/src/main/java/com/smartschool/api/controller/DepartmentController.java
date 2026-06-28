package com.smartschool.api.controller;

import com.smartschool.api.entity.Department;
import com.smartschool.api.entity.DepartmentHead;
import com.smartschool.api.entity.DepartmentPermission;
import com.smartschool.api.entity.DepartmentType;
import com.smartschool.api.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin("*")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // ==================== DEPARTMENT MANAGEMENT ====================

    /**
     * POST /api/departments/create
     * Principal creates a new department
     */
    @PostMapping("/create")
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        try {
            if (department.getSchool() == null || department.getSchool().getId() == null) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("error", "schoolId is required");
                }});
            }
            if (department.getDeptName() == null || department.getDeptName().isEmpty()) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("error", "deptName is required");
                }});
            }
            if (department.getDeptType() == null) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("error", "deptType is required (ADMISSION, EXAM, BUS)");
                }});
            }
            if (department.getUsername() == null || department.getUsername().isEmpty()) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("error", "username is required");
                }});
            }
            if (department.getPassword() == null || department.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("error", "password is required");
                }});
            }

            Department createdDept = departmentService.createDepartment(department);
            Map<String, Object> response = new HashMap<>();
            response.put("department", createdDept);
            response.put("login", new HashMap<String, String>() {{
                put("username", createdDept.getUsername());
                put("password", createdDept.getPassword());
            }});
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * GET /api/departments/school/{schoolId}
     * Get all departments in a school
     */
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<?> getDepartmentsBySchool(@PathVariable Long schoolId) {
        try {
            List<Department> departments = departmentService.getDepartmentsBySchool(schoolId);
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * GET /api/departments/{id}
     * Get department by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        try {
            Optional<Department> dept = departmentService.getDepartmentById(id);
            if (dept.isPresent()) {
                return ResponseEntity.ok(dept.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, String>() {{
                        put("error", "Department not found");
                    }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * GET /api/departments/type/{schoolId}/{deptType}
     * Get department by type (ADMISSION, EXAM, BUS)
     */
    @GetMapping("/type/{schoolId}/{deptType}")
    public ResponseEntity<?> getDepartmentByType(@PathVariable Long schoolId, @PathVariable String deptType) {
        try {
            DepartmentType type = DepartmentType.valueOf(deptType.toUpperCase());
            Optional<Department> dept = departmentService.getDepartmentByType(schoolId, type);
            if (dept.isPresent()) {
                return ResponseEntity.ok(dept.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, String>() {{
                        put("error", "Department of type " + deptType + " not found");
                    }});
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Invalid department type. Must be ADMISSION, EXAM, or BUS");
            }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * PUT /api/departments/{id}
     * Update department
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department departmentDetails) {
        try {
            Department updated = departmentService.updateDepartment(id, departmentDetails);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, String>() {{
                        put("error", "Department not found");
                    }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * DELETE /api/departments/{id}
     * Delete department
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            Optional<Department> dept = departmentService.getDepartmentById(id);
            if (dept.isPresent()) {
                departmentService.deleteDepartment(id);
                return ResponseEntity.ok(new HashMap<String, String>() {{
                    put("message", "Department deleted successfully");
                }});
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, String>() {{
                        put("error", "Department not found");
                    }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // ==================== DEPARTMENT HEAD MANAGEMENT ====================

    /**
     * POST /api/departments/{deptId}/assign-head
     * Assign a teacher as department head
     */
    @PostMapping("/{deptId}/assign-head")
    public ResponseEntity<?> assignDepartmentHead(@PathVariable Long deptId, @RequestBody DepartmentHead departmentHead) {
        try {
            Optional<Department> dept = departmentService.getDepartmentById(deptId);
            if (dept.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new HashMap<String, String>() {{
                            put("error", "Department not found");
                        }});
            }

            departmentHead.setDepartment(dept.get());
            DepartmentHead assigned = departmentService.assignDepartmentHead(departmentHead);
            return ResponseEntity.status(HttpStatus.CREATED).body(assigned);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * GET /api/departments/{deptId}/head
     * Get department head
     */
    @GetMapping("/{deptId}/head")
    public ResponseEntity<?> getDepartmentHead(@PathVariable Long deptId) {
        try {
            Optional<DepartmentHead> head = departmentService.getDepartmentHead(deptId);
            if (head.isPresent()) {
                return ResponseEntity.ok(head.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, String>() {{
                        put("error", "No department head assigned");
                    }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * DELETE /api/departments/{deptId}/head
     * Remove department head
     */
    @DeleteMapping("/{deptId}/head")
    public ResponseEntity<?> removeDepartmentHead(@PathVariable Long deptId) {
        try {
            Optional<DepartmentHead> head = departmentService.getDepartmentHead(deptId);
            if (head.isPresent()) {
                departmentService.removeDepartmentHead(deptId);
                return ResponseEntity.ok(new HashMap<String, String>() {{
                    put("message", "Department head removed successfully");
                }});
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, String>() {{
                        put("error", "No department head found");
                    }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // ==================== PERMISSION MANAGEMENT ====================

    /**
     * POST /api/departments/{deptId}/permissions
     * Add permission to a department
     */
    @PostMapping("/{deptId}/permissions")
    public ResponseEntity<?> addPermission(@PathVariable Long deptId, @RequestBody DepartmentPermission permission) {
        try {
            Optional<Department> dept = departmentService.getDepartmentById(deptId);
            if (dept.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new HashMap<String, String>() {{
                            put("error", "Department not found");
                        }});
            }

            permission.setDepartment(dept.get());
            if (permission.getIsActive() == null) {
                permission.setIsActive(true);
            }
            DepartmentPermission saved = departmentService.addPermission(permission);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * GET /api/departments/{deptId}/permissions
     * Get all permissions for a department
     */
    @GetMapping("/{deptId}/permissions")
    public ResponseEntity<?> getPermissions(@PathVariable Long deptId) {
        try {
            List<DepartmentPermission> permissions = departmentService.getPermissions(deptId);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * GET /api/departments/{deptId}/permissions/active
     * Get all active permissions for a department
     */
    @GetMapping("/{deptId}/permissions/active")
    public ResponseEntity<?> getActivePermissions(@PathVariable Long deptId) {
        try {
            List<DepartmentPermission> permissions = departmentService.getActivePermissions(deptId);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * PUT /api/departments/permissions/{permId}
     * Update permission
     */
    @PutMapping("/permissions/{permId}")
    public ResponseEntity<?> updatePermission(@PathVariable Long permId, @RequestBody DepartmentPermission permissionDetails) {
        try {
            DepartmentPermission updated = departmentService.updatePermission(permId, permissionDetails);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, String>() {{
                        put("error", "Permission not found");
                    }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    /**
     * DELETE /api/departments/permissions/{permId}
     * Delete permission
     */
    @DeleteMapping("/permissions/{permId}")
    public ResponseEntity<?> deletePermission(@PathVariable Long permId) {
        try {
            departmentService.removePermission(permId);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "Permission deleted successfully");
            }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }
}
