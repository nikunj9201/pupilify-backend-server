package com.smartschool.api.controller;

import com.smartschool.api.entity.Bus;
import com.smartschool.api.entity.School;
import com.smartschool.api.repository.BusRepository;
import com.smartschool.api.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/buses")
@CrossOrigin("*")
public class BusController {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<Bus>> getBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(busRepository.findBySchoolId(schoolId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bus> getById(@PathVariable Long id) {
        Optional<Bus> b = busRepository.findById(id);
        return b.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Bus payload) {
        if (payload.getSchool() == null || payload.getSchool().getId() == null) {
            return ResponseEntity.badRequest().body("schoolId is required in payload.school.id");
        }
        Optional<School> s = schoolRepository.findById(payload.getSchool().getId());
        if (s.isEmpty()) return ResponseEntity.badRequest().body("School not found");
        if (busRepository.existsByRegistrationNo(payload.getRegistrationNo())) {
            return ResponseEntity.badRequest().body("Registration number already exists");
        }
        payload.setSchool(s.get());
        Bus saved = busRepository.save(payload);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Bus payload) {
        Optional<Bus> b = busRepository.findById(id);
        if (b.isEmpty()) return ResponseEntity.notFound().build();
        Bus existing = b.get();
        existing.setRegistrationNo(payload.getRegistrationNo());
        existing.setCapacity(payload.getCapacity());
        existing.setDriverName(payload.getDriverName());
        if (payload.getSchool() != null && payload.getSchool().getId() != null) {
            schoolRepository.findById(payload.getSchool().getId()).ifPresent(existing::setSchool);
        }
        busRepository.save(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!busRepository.existsById(id)) return ResponseEntity.notFound().build();
        busRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}

