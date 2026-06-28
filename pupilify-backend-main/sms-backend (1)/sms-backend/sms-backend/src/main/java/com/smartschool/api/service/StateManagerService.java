package com.smartschool.api.service;

import com.smartschool.api.entity.StateManager;
import com.smartschool.api.repository.StateManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StateManagerService {

    @Autowired
    private StateManagerRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public StateManager create(StateManager manager) {
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        return repository.save(manager);
    }

    public List<StateManager> getAll() {
        return repository.findAll();
    }

    public Optional<StateManager> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<StateManager> getByEmail(String email) {
        return repository.findByEmail(email);
    }

    public StateManager update(Long id, StateManager updated) {
        return repository.findById(id).map(manager -> {
            manager.setName(updated.getName());
            manager.setEmail(updated.getEmail());
            manager.setStateId(updated.getStateId());
            manager.setActive(updated.getActive());
            if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
                manager.setPassword(passwordEncoder.encode(updated.getPassword()));
            }
            return repository.save(manager);
        }).orElseThrow(() -> new RuntimeException("StateManager not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
