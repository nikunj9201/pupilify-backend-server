package com.smartschool.api.repository;

import com.smartschool.api.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByStateId(Long stateId);
}

