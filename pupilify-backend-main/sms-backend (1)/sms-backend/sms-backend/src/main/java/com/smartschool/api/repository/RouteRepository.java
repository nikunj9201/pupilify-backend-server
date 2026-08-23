package com.smartschool.api.repository;

import com.smartschool.api.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findBySchoolIdAndBusIdAndActiveTrue(Long schoolId, Long busId);
    List<Route> findBySchoolIdAndActiveTrue(Long schoolId);
    // keep originals if needed
    List<Route> findBySchoolIdAndBusId(Long schoolId, Long busId);
    List<Route> findBySchoolId(Long schoolId);
    long countByBusId(Long busId);

    // find routes for a bus
    List<Route> findByBusId(Long busId);
}