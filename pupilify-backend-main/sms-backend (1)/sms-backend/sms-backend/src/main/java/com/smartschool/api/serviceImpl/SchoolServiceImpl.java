package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.SchoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID; // 🚩 Naya import unique ID ke liye

@Service
public class SchoolServiceImpl implements SchoolService {

    private static final Logger log = LoggerFactory.getLogger(SchoolServiceImpl.class);

    @Autowired private SchoolRepository schoolRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AcademicYearConfigRepository yearRepo;

    @Autowired private StudentRepository studentRepository;
    @Autowired private TeacherRepository teacherRepository;

    @Autowired private StateRepository stateRepository;
    @Autowired private DistrictRepository districtRepository;
    @Autowired private StateManagerRepository stateManagerRepository;
    @Autowired private DistrictManagerRepository districtManagerRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private String getSchoolUploadDir() {
        return uploadDir + "/schools/";
    }

    @Override
    @Transactional
    public School createSchoolWithYear(School school, Long currentYearId, MultipartFile logo) throws IOException {
        log.info("Creating school: {} with Logo", school.getSchoolName());

        if (schoolRepository.existsByMailId(school.getMailId())) {
            throw new RuntimeException("Error: School with this Email already exists!");
        }

        if (logo != null && !logo.isEmpty()) {
            school.setSchoolLogo(saveLogo(logo));
        }

        if (currentYearId != null) {
            AcademicYearConfig year = yearRepo.findById(currentYearId).orElseThrow();
            school.setCurrentYear(year);
        }

        School savedSchool = schoolRepository.save(school);

        User principal = new User();
        principal.setUsername(school.getMailId());
        principal.setPassword(passwordEncoder.encode(school.getPassword()));
        principal.setRole(Role.ROLE_ADMIN);
        principal.setSchool(savedSchool);
        principal.setActive(true);
        userRepository.save(principal);

        return savedSchool;
    }

    @Override
    @Transactional
    public School updateSchool(Long id, School details, MultipartFile logo) throws IOException {
        School school = getSchoolById(id);

        String oldEmail = school.getMailId();
        String newEmail = details.getMailId();

        SubscriptionStatus oldStatus = school.getSubscriptionStatus();
        SubscriptionStatus newStatus = details.getSubscriptionStatus();

        school.setSchoolName(details.getSchoolName());
        school.setAddress(details.getAddress());
        school.setPhoneNumber(details.getPhoneNumber());
        school.setSubscriptionStatus(newStatus);
        school.setMailId(newEmail);

        if (logo != null && !logo.isEmpty()) {
            school.setSchoolLogo(saveLogo(logo));
        }

        School updatedSchool = schoolRepository.save(school);

        if (newEmail != null && !newEmail.equals(oldEmail)) {
            userRepository.updateUsernameBySchoolId(id, newEmail);
            log.info("Email updated in User table for School ID {}: {}", id, newEmail);
        }

        if (oldStatus != newStatus) {
            boolean shouldBeActive = (newStatus != SubscriptionStatus.INACTIVE);
            userRepository.updateUserStatusBySchoolId(id, shouldBeActive);
            log.info("Subscription changed for School ID {}: Users active status set to {}", id, shouldBeActive);
        }

        return updatedSchool;
    }

    @Override
    @Transactional
    public void deleteSchool(Long id) {
        // 🚩 Fixed Soft Delete Logic to prevent Duplicate Entry error
        School school = getSchoolById(id);

        String deletedTimestamp = String.valueOf(System.currentTimeMillis());
        String deletedEmail = "deleted_" + deletedTimestamp + "_" + school.getMailId();

        // 1. School Table Update
        school.setMailId(deletedEmail);
        school.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
        schoolRepository.save(school);

        // 2. User Table Update - Loop chala kar har user ko unique banaya
        List<User> users = userRepository.findBySchoolId(id);
        for (User user : users) {
            // Har user ke liye unique deleted username generate karna (UUID ke saath)
            String uniqueDeletedUser = "deleted_" + UUID.randomUUID().toString().substring(0, 8) + "_" + user.getUsername();
            user.setUsername(uniqueDeletedUser);
            user.setActive(false);
        }
        userRepository.saveAll(users); // Sabko ek saath save kiya

        // 3. Student Table Update
        studentRepository.deactivateStudentsBySchoolId(id);

        // 4. Teacher Table Update
        teacherRepository.deactivateTeachersBySchoolId(id);

        log.info("School ID {}, its Users, Students, and Teachers have been uniquely deactivated.", id);
    }

    private String saveLogo(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(getSchoolUploadDir());
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @Override public List<School> getAllSchools() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return schoolRepository.findAll();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        String username = auth.getName();

        if ("ROLE_STATE_ADMIN".equals(role)) {
            Long stateId = stateManagerRepository.findByEmail(username).map(StateManager::getStateId).orElse(null);
            if (stateId != null) return schoolRepository.findByStateId(stateId);
        } else if ("ROLE_DISTRICT_ADMIN".equals(role)) {
            Long districtId = districtManagerRepository.findByEmail(username).map(DistrictManager::getDistrictId).orElse(null);
            if (districtId != null) return schoolRepository.findByDistrictId(districtId);
        }
        return schoolRepository.findAll();
    }
    @Override public School getSchoolById(Long id) { return schoolRepository.findById(id).orElseThrow(); }

    @Override public School createSchool(School s) {
        try { return createSchoolWithYear(s, null, null); } catch(Exception e) { return null; }
    }

    @Override
    @Transactional
    public School updateSchoolYear(Long id, Long yearId) {
        School s = getSchoolById(id);
        s.setCurrentYear(yearRepo.findById(yearId)
                .orElseThrow(() -> new RuntimeException("Academic Year Config not found for ID: " + yearId)));
        return schoolRepository.save(s);
    }

    @Override
    public List<School> getSchoolsByState(Long stateId) {
        return schoolRepository.findByStateId(stateId);
    }

    @Override
    public List<School> getSchoolsByDistrict(Long districtId) {
        return schoolRepository.findByDistrictId(districtId);
    }

    @Override
    @Transactional
    public School assignStateToSchool(Long schoolId, Long stateId) {
        School school = getSchoolById(schoolId);
        State state = stateRepository.findById(stateId)
                .orElseThrow(() -> new RuntimeException("State not found with ID: " + stateId));
        school.setState(state);
        return schoolRepository.save(school);
    }

    @Override
    @Transactional
    public School assignDistrictToSchool(Long schoolId, Long districtId) {
        School school = getSchoolById(schoolId);
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new RuntimeException("District not found with ID: " + districtId));
        school.setDistrict(district);
        return schoolRepository.save(school);
    }
}