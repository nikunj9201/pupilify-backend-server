package com.smartschool.api.repository;

import com.smartschool.api.entity.Stoppage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoppageRepository extends JpaRepository<Stoppage, Long> {
    List<Stoppage> findByRouteId(Long routeId);
}