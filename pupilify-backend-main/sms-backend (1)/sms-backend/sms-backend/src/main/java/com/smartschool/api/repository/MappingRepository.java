package com.smartschool.api.repository;

import com.smartschool.api.entity.ClassTeacherMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingRepository extends JpaRepository<ClassTeacherMapping, Long> {

    // Check: Kya koi teacher is class mein assigned hai?
    boolean existsBySchoolClassId(Long classId);

    // Check: Kya koi teacher is specific section mein assigned hai?
    boolean existsBySectionId(Long sectionId);
}