package com.smartschool.api.repository;

import com.smartschool.api.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    Optional<School> findByMailId(String mailId);

    Boolean existsByMailId(String mailId);

    Optional<School> findBySchoolName(String schoolName);

    // 🚩 NEW: Kisi specific year mein kitne schools active hain
    List<School> findByCurrentYearId(Long academicYearId);

    // 🚩 NEW: Schools by state and district
    List<School> findByStateId(Long stateId);
    List<School> findByDistrictId(Long districtId);
}