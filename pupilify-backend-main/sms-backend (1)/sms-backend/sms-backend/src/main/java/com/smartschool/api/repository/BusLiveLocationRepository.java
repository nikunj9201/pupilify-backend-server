package com.smartschool.api.repository;

import com.smartschool.api.entity.BusLiveLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusLiveLocationRepository extends JpaRepository<BusLiveLocation, Long> {

    // Custom query for Principal Portal: Get all active buses for a specific school
    List<BusLiveLocation> findAllByBus_School_IdAndIsTrackingActiveTrue(Long schoolId);
}