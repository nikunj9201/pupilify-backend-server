package com.smartschool.api.repository;

import com.smartschool.api.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findBySchoolIdAndBusId(Long schoolId, Long busId);
    List<Route> findBySchoolId(Long schoolId);
}