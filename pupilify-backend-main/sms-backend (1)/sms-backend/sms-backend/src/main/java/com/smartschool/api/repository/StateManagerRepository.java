package com.smartschool.api.repository;

import com.smartschool.api.entity.StateManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateManagerRepository extends JpaRepository<StateManager, Long> {
    Optional<StateManager> findByEmail(String email);
}
