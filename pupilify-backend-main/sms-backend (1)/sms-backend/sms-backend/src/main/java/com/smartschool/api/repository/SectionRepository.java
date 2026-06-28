package com.smartschool.api.repository;

import com.smartschool.api.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // School ki saari active sections (global list ke liye)
    List<Section> findBySchoolIdAndIsActiveTrue(Long schoolId);

    // ✅ NEW: Ek specific class ki active sections fetch karne ke liye
    // Frontend class select karne par yeh call karta hai
    List<Section> findBySchoolClassIdAndIsActiveTrue(Long classId);

    // ✅ NEW: School + Class dono filter karo (safe for multi-school)
    List<Section> findBySchoolClassIdAndSchoolIdAndIsActiveTrue(Long classId, Long schoolId);
}