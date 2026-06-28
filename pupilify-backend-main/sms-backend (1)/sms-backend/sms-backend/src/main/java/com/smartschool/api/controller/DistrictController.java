package com.smartschool.api.controller;

import com.smartschool.api.entity.District;
import com.smartschool.api.entity.State;
import com.smartschool.api.repository.DistrictRepository;
import com.smartschool.api.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/district")
@CrossOrigin("*")
public class DistrictController {

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/all")
    public ResponseEntity<List<District>> getAll(@RequestParam(required = false) Long stateId) {
        if (stateId != null) return ResponseEntity.ok(districtRepository.findByStateId(stateId));
        return ResponseEntity.ok(districtRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<District> getById(@PathVariable Long id) {
        Optional<District> d = districtRepository.findById(id);
        return d.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody District payload) {
        // payload should contain state.id (or state object)
        if (payload.getState() == null || payload.getState().getId() == null) {
            return ResponseEntity.badRequest().body("stateId is required in payload.state.id");
        }
        Optional<State> st = stateRepository.findById(payload.getState().getId());
        if (st.isEmpty()) return ResponseEntity.badRequest().body("State not found");
        payload.setState(st.get());
        District saved = districtRepository.save(payload);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody District payload) {
        Optional<District> d = districtRepository.findById(id);
        if (d.isEmpty()) return ResponseEntity.notFound().build();
        District existing = d.get();
        existing.setName(payload.getName());
        existing.setCode(payload.getCode());
        if (payload.getState() != null && payload.getState().getId() != null) {
            stateRepository.findById(payload.getState().getId()).ifPresent(existing::setState);
        }
        districtRepository.save(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!districtRepository.existsById(id)) return ResponseEntity.notFound().build();
        districtRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}

