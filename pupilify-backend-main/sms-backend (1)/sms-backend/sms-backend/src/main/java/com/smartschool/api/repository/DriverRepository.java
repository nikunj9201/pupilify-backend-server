package com.smartschool.api.repository;

import com.smartschool.api.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByBusSchoolId(Long schoolId);
    Optional<Driver> findByIdAndBusSchoolId(Long id, Long schoolId);
}