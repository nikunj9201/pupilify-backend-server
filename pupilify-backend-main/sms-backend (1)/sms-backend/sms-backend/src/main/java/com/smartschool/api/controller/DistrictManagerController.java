package com.smartschool.api.controller;

import com.smartschool.api.entity.DistrictManager;
import com.smartschool.api.service.DistrictManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin/district-managers")
public class DistrictManagerController {

    @Autowired
    private DistrictManagerService service;

    @PostMapping("/create")
    public ResponseEntity<DistrictManager> create(@RequestBody DistrictManager manager) {
        return ResponseEntity.ok(service.create(manager));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DistrictManager>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistrictManager> getById(@PathVariable Long id) {
        return service.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<DistrictManager>> getByStateId(@PathVariable Long stateId) {
        return ResponseEntity.ok(service.getByStateId(stateId));
    }

    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<DistrictManager>> getByDistrictId(@PathVariable Long districtId) {
        return ResponseEntity.ok(service.getByDistrictId(districtId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DistrictManager> update(@PathVariable Long id, @RequestBody DistrictManager manager) {
        return ResponseEntity.ok(service.update(id, manager));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
