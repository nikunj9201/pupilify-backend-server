package com.smartschool.api.repository;

import com.smartschool.api.entity.BusFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BusFeeRepository extends JpaRepository<BusFee, Long> {
    List<BusFee> findBySchoolId(Long schoolId);
    List<BusFee> findBySchoolIdAndAcademicYearId(Long schoolId, Long academicYearId);
}

