package com.smartschool.api.controller;

import com.smartschool.api.entity.PrincipalDepartmentAssignment;
import com.smartschool.api.entity.User;
import com.smartschool.api.repository.PrincipalDepartmentAssignmentRepository;
import com.smartschool.api.repository.UserRepository;
import com.smartschool.api.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/principals")
@CrossOrigin("*")
public class PrincipalController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private PrincipalDepartmentAssignmentRepository assignmentRepository;

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<User>> getPrincipalsBySchool(@PathVariable Long schoolId) {
        // find users by school and filter by ROLE if needed on client-side
        return ResponseEntity.ok(userRepository.findBySchoolId(schoolId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        Optional<User> u = userRepository.findById(id);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody User payload) {
        if (payload.getSchool() == null || payload.getSchool().getId() == null) {
            return ResponseEntity.badRequest().body("schoolId is required in payload.school.id");
        }
        if (payload.getUsername() == null || payload.getPassword() == null) {
            return ResponseEntity.badRequest().body("username and password required");
        }
        if (userRepository.existsByUsername(payload.getUsername())) {
            return ResponseEntity.badRequest().body("username already exists");
        }
        // Note: password should be encoded by service layer; this controller keeps it simple
        userRepository.save(payload);
        return ResponseEntity.ok(payload);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User payload) {
        Optional<User> u = userRepository.findById(id);
        if (u.isEmpty()) return ResponseEntity.notFound().build();
        User existing = u.get();
        existing.setUsername(payload.getUsername());
        existing.setActive(payload.isActive());
        existing.setRole(payload.getRole());
        userRepository.save(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/assignments/{principalUserId}")
    public ResponseEntity<List<PrincipalDepartmentAssignment>> getAssignments(@PathVariable Long principalUserId) {
        return ResponseEntity.ok(assignmentRepository.findByPrincipalId(principalUserId));
    }
}

