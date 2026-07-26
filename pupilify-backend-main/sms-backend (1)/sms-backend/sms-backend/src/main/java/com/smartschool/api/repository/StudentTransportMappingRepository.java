package com.smartschool.api.repository;

import com.smartschool.api.entity.StudentTransportMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentTransportMappingRepository extends JpaRepository<StudentTransportMapping, Long> {
    Optional<StudentTransportMapping> findByStudentId(Long studentId);
    List<StudentTransportMapping> findAllByRoute_Bus_Id(Long busId);
}