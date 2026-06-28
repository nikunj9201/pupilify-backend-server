package com.smartschool.api.service;

import com.smartschool.api.entity.School;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface SchoolService {

    // 🚩 Updated: Logo upload support ke saath
    School createSchoolWithYear(School school, Long currentYearId, MultipartFile logo) throws IOException;

    List<School> getAllSchools();

    School getSchoolById(Long id);

    // 🚩 Updated: Update ke waqt bhi logo change karne ka option
    School updateSchool(Long id, School schoolDetails, MultipartFile logo) throws IOException;

    School updateSchoolYear(Long id, Long newYearId);

    void deleteSchool(Long id);

    // Purana method compatibility ke liye
    School createSchool(School school);

    List<School> getSchoolsByState(Long stateId);
    List<School> getSchoolsByDistrict(Long districtId);

    // Naye methods: School ko state aur district assign karne ke liye
    School assignStateToSchool(Long schoolId, Long stateId);
    School assignDistrictToSchool(Long schoolId, Long districtId);
}