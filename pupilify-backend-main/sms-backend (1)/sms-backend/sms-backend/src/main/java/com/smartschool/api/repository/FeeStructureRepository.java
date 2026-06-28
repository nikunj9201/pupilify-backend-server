package com.smartschool.api.repository;

import com.smartschool.api.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    List<FeeStructure> findBySchoolIdAndIsActiveTrue(Long schoolId);

    // 🚩 Updated: Year ID based fetch [cite: 147, 161]
    List<FeeStructure> findBySchoolIdAndAcademicYearIdAndIsActiveTrue(Long schoolId, Long yearId);

    Optional<FeeStructure> findBySchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(Long classId, Long sectionId, Long yearId);

    Optional<FeeStructure> findBySchoolClassIdAndSectionIsNullAndAcademicYearIdAndIsActiveTrue(Long classId, Long yearId);

    boolean existsBySchoolClassIdAndIsActiveTrue(Long classId);
    boolean existsBySectionIdAndIsActiveTrue(Long sectionId);
}