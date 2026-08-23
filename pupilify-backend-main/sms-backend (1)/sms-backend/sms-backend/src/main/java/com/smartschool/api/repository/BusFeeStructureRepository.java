package com.smartschool.api.repository;

import com.smartschool.api.entity.BusFeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusFeeStructureRepository extends JpaRepository<BusFeeStructure, Long> {
    List<BusFeeStructure> findByBusId(Long busId);
    void deleteBySchoolIdAndAcademicYearId(Long schoolId, Long academicYearId);
}