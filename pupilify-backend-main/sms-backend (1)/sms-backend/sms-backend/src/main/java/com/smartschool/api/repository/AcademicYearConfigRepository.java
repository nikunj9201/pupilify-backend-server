package com.smartschool.api.repository;

import com.smartschool.api.entity.AcademicYearConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicYearConfigRepository
        extends JpaRepository<AcademicYearConfig, Long> {

    // ✅ School-wise current year fetch karo
    Optional<AcademicYearConfig> findTopBySchoolIdOrderByIdDesc(Long schoolId);

    // ✅ School ki saari year configs (history ke liye)
    List<AcademicYearConfig> findBySchoolIdOrderByIdDesc(Long schoolId);

    // ✅ School ka active/current year (status = ACTIVE)
    Optional<AcademicYearConfig>
    findTopBySchoolIdAndStatusOrderByIdDesc(
            Long schoolId,
            AcademicYearConfig.YearChangeStatus status);

    // ✅ Create karte time check karo: duplicate year toh nahi
    boolean existsBySchoolIdAndCurrentYear(Long schoolId, String year);
}
