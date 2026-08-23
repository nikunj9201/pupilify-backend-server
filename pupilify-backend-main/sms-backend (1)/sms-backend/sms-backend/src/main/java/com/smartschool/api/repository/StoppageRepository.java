package com.smartschool.api.repository;

import com.smartschool.api.entity.Stoppage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoppageRepository extends JpaRepository<Stoppage, Long> {
    // Return only active stoppages by default
    List<Stoppage> findByRouteIdAndActiveTrue(Long routeId);

    // Keep original if needed elsewhere
    List<Stoppage> findByRouteId(Long routeId);
}