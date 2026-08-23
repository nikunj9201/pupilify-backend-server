package com.smartschool.api.repository;

import com.smartschool.api.entity.BusFeeRateStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusFeeRateStructureRepository extends JpaRepository<BusFeeRateStructure, Long> {
    Optional<BusFeeRateStructure> findByStoppageIdAndBusIdAndSchoolIdAndAcademicYearId(
            Long stoppageId, Long busId, Long schoolId, Long academicYearId);
    List<BusFeeRateStructure> findByBusIdAndSchoolIdAndAcademicYearIdAndActiveTrue(
            Long busId, Long schoolId, Long academicYearId);
    List<BusFeeRateStructure> findByStoppageIdAndAcademicYearIdAndActiveTrue(Long stoppageId, Long academicYearId);
    List<BusFeeRateStructure> findBySchoolIdAndAcademicYearIdAndActiveTrue(Long schoolId, Long academicYearId);
}

