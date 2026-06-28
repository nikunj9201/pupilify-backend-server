package com.smartschool.api.repository;

import com.smartschool.api.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findBySchoolId(Long schoolId);
    boolean existsByRegistrationNo(String registrationNo);
}

