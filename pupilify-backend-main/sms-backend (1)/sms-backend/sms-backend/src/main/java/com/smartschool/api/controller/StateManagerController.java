package com.smartschool.api.controller;

import com.smartschool.api.entity.StateManager;
import com.smartschool.api.service.StateManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin/state-managers")
public class StateManagerController {

    @Autowired
    private StateManagerService service;

    @PostMapping("/create")
    public ResponseEntity<StateManager> create(@RequestBody StateManager manager) {
        return ResponseEntity.ok(service.create(manager));
    }

    @GetMapping("/all")
    public ResponseEntity<List<StateManager>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StateManager> getById(@PathVariable Long id) {
        return service.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<StateManager> update(@PathVariable Long id, @RequestBody StateManager manager) {
        return ResponseEntity.ok(service.update(id, manager));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
