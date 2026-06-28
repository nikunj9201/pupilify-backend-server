package com.smartschool.api.repository;

import com.smartschool.api.entity.BusBoardingPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BusBoardingPointRepository extends JpaRepository<BusBoardingPoint, Long> {
    List<BusBoardingPoint> findByBusIdOrderBySeq(Long busId);
}

