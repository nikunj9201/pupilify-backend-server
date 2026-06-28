package com.smartschool.api.repository;

import com.smartschool.api.entity.DistrictManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictManagerRepository extends JpaRepository<DistrictManager, Long> {
    Optional<DistrictManager> findByEmail(String email);
    List<DistrictManager> findByStateId(Long stateId);
    List<DistrictManager> findByDistrictId(Long districtId);
}
