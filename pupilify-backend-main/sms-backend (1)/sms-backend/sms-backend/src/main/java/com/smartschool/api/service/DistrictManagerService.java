package com.smartschool.api.service;

import com.smartschool.api.entity.DistrictManager;
import com.smartschool.api.repository.DistrictManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DistrictManagerService {

    @Autowired
    private DistrictManagerRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DistrictManager create(DistrictManager manager) {
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        return repository.save(manager);
    }

    public List<DistrictManager> getAll() {
        return repository.findAll();
    }

    public Optional<DistrictManager> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<DistrictManager> getByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<DistrictManager> getByStateId(Long stateId) {
        return repository.findByStateId(stateId);
    }

    public List<DistrictManager> getByDistrictId(Long districtId) {
        return repository.findByDistrictId(districtId);
    }

    public DistrictManager update(Long id, DistrictManager updated) {
        return repository.findById(id).map(manager -> {
            manager.setName(updated.getName());
            manager.setEmail(updated.getEmail());
            manager.setDistrictId(updated.getDistrictId());
            manager.setStateId(updated.getStateId());
            manager.setActive(updated.getActive());
            if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
                manager.setPassword(passwordEncoder.encode(updated.getPassword()));
            }
            return repository.save(manager);
        }).orElseThrow(() -> new RuntimeException("DistrictManager not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
