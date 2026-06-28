package com.smartschool.api.controller;

import com.smartschool.api.entity.State;
import com.smartschool.api.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/superadmin/state")
@CrossOrigin("*")
public class StateController {

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/all")
    public ResponseEntity<List<State>> getAll() {
        return ResponseEntity.ok(stateRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<State> getById(@PathVariable Long id) {
        Optional<State> s = stateRepository.findById(id);
        return s.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<State> create(@RequestBody State state) {
        State saved = stateRepository.save(state);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<State> update(@PathVariable Long id, @RequestBody State payload) {
        Optional<State> s = stateRepository.findById(id);
        if (s.isEmpty()) return ResponseEntity.notFound().build();
        State existing = s.get();
        existing.setName(payload.getName());
        existing.setCode(payload.getCode());
        stateRepository.save(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!stateRepository.existsById(id)) return ResponseEntity.notFound().build();
        stateRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}

