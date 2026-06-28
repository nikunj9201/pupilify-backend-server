package com.smartschool.api.repository;

import com.smartschool.api.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassRepository extends JpaRepository<SchoolClass, Long> {
    // Sirf active classes dikhane ke liye dashboard par
    List<SchoolClass> findBySchoolIdAndIsActiveTrue(Long schoolId);
}