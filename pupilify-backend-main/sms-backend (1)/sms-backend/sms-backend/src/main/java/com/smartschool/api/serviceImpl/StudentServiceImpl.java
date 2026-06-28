package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private SchoolRepository schoolRepository;
    @Autowired private ClassRepository classRepository;
    @Autowired private SectionRepository sectionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AcademicYearConfigRepository yearRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private String getStudentUploadDir() {
        return uploadDir + "/students/";
    }

    @Override
    @Transactional
    public Student onboardStudent(Student student, MultipartFile photo, MultipartFile marksheet,
                                  MultipartFile tc, MultipartFile aadharImg, MultipartFile samagraImg,
                                  MultipartFile passbookImg, Long schoolId, Long classId, Long sectionId) throws IOException {
        return onboardStudentWithApaar(student, photo, marksheet, tc, aadharImg, samagraImg, passbookImg, null, null, null, null, null, schoolId, classId, sectionId, null);
    }

    @Override
    @Transactional
    public Student onboardStudentWithApaar(Student student, MultipartFile photo, MultipartFile marksheet,
                                           MultipartFile tc, MultipartFile aadharImg, MultipartFile samagraImg,
                                           MultipartFile passbookImg, MultipartFile apaarImg,
                                           MultipartFile birthCertificate, MultipartFile incomeCertificate,
                                           MultipartFile castCertificate, MultipartFile domicileCertificate,
                                           Long schoolId, Long classId, Long sectionId, Long academicYearId) throws IOException {

        Optional<User> existingUser = userRepository.findByUsername(student.getEmail());
        if (existingUser.isPresent() && existingUser.get().isActive()) {
            throw new RuntimeException("Error: This email is already active!");
        }

        student.setEnrollmentId(generateEnrollmentId());
        student.setActive(true);

        File directory = new File(getStudentUploadDir());
        if (!directory.exists()) directory.mkdirs();

        student.setStudentPhoto(saveFile(photo, "STU_"));
        student.setLastClassMarksheet(saveFile(marksheet, "MRK_"));
        if (tc != null && !tc.isEmpty()) student.setTcImage(saveFile(tc, "TC_"));
        student.setAadharCardImage(saveFile(aadharImg, "ADR_"));
        student.setSamagraIdImage(saveFile(samagraImg, "SAM_"));
        student.setBankPassbookImage(saveFile(passbookImg, "BNK_"));
        if (apaarImg != null && !apaarImg.isEmpty()) student.setApaarCardImage(saveFile(apaarImg, "APR_"));
        if (birthCertificate != null && !birthCertificate.isEmpty()) student.setBirthCertificate(saveFile(birthCertificate, "BC_"));
        if (incomeCertificate != null && !incomeCertificate.isEmpty()) student.setIncomeCertificate(saveFile(incomeCertificate, "IC_"));
        if (castCertificate != null && !castCertificate.isEmpty()) student.setCastCertificate(saveFile(castCertificate, "CC_"));
        if (domicileCertificate != null && !domicileCertificate.isEmpty()) student.setDomicileCertificate(saveFile(domicileCertificate, "DC_"));


        School school = schoolRepository.findById(schoolId).orElseThrow();
        SchoolClass schoolClass = classRepository.findById(classId).orElseThrow();

        student.setSchool(school);
        student.setSchoolClass(schoolClass);

        if (academicYearId != null) {
            AcademicYearConfig yearConfig = yearRepo.findById(academicYearId).orElseThrow();
            student.setAcademicYear(yearConfig);
        }

        if (sectionId != null && sectionId > 0) {
            student.setSection(sectionRepository.findById(sectionId).orElseThrow());
        }

        User user = new User();
        user.setUsername(student.getEmail());
        user.setPassword(passwordEncoder.encode(student.getPassword()));
        user.setRole(Role.ROLE_STUDENT);
        user.setSchool(school);
        user.setActive(true);
        student.setUser(user);

        return studentRepository.save(student);
    }

    // 🚩🚩 NAYA METHOD: UPDATE STUDENT WITH FILES 🚩🚩
    @Override
    @Transactional
    public Student updateStudent(Long studentId, Student details,
                                 MultipartFile photo, MultipartFile marksheet,
                                 MultipartFile tc, MultipartFile aadharImg,
                                 MultipartFile samagraImg, MultipartFile passbookImg,
                                 MultipartFile apaarImg, MultipartFile birthCertificate,
                                 MultipartFile incomeCertificate, MultipartFile castCertificate,
                                 MultipartFile domicileCertificate) throws IOException {

        Student student = getStudentById(studentId);

        // 🚩 IMPORTANT: Fields that CANNOT be updated (Onboarding time se set fields)
        // ❌ name, dob, email, password - Ye onboarding mein set hote hain
        // ❌ schoolId, classId, sectionId - Ye promotion/enrollment change se change hote hain
        // ✅ Sirf ye fields update kar sakte ho:

        // 1. Update Optional/Editable Fields
        if(details.getPhoneNo() != null) student.setPhoneNo(details.getPhoneNo());
        if(details.getAddress() != null) student.setAddress(details.getAddress());
        if(details.getGender() != null) student.setGender(details.getGender());
        if(details.getCaste() != null) student.setCaste(details.getCaste());
        if(details.getFatherName() != null) student.setFatherName(details.getFatherName());
        if(details.getMotherName() != null) student.setMotherName(details.getMotherName());
        if(details.getFatherContactNumber() != null) student.setFatherContactNumber(details.getFatherContactNumber());
        if(details.getAadharCardNo() != null) student.setAadharCardNo(details.getAadharCardNo());
        if(details.getSamagraId() != null) student.setSamagraId(details.getSamagraId());
        if(details.getRollNumber() != null) student.setRollNumber(details.getRollNumber());
        if(details.getFamilyId() != null) student.setFamilyId(details.getFamilyId());
        if(details.getScholarNo() != null) student.setScholarNo(details.getScholarNo());
        if(details.getFatherOccupation() != null) student.setFatherOccupation(details.getFatherOccupation());
        if(details.getFatherSalary() != null) student.setFatherSalary(details.getFatherSalary());
        if(details.getPostalCode() != null) student.setPostalCode(details.getPostalCode());
        if(details.getBankName() != null) student.setBankName(details.getBankName());
        if(details.getBankAccountNo() != null) student.setBankAccountNo(details.getBankAccountNo());
        if(details.getIfscCode() != null) student.setIfscCode(details.getIfscCode());
        if(details.getBranch() != null) student.setBranch(details.getBranch());


        // 2. Files Update (Only if new file is provided)
        if (photo != null && !photo.isEmpty()) student.setStudentPhoto(saveFile(photo, "STU_"));
        if (marksheet != null && !marksheet.isEmpty()) student.setLastClassMarksheet(saveFile(marksheet, "MRK_"));
        if (tc != null && !tc.isEmpty()) student.setTcImage(saveFile(tc, "TC_"));
        if (aadharImg != null && !aadharImg.isEmpty()) student.setAadharCardImage(saveFile(aadharImg, "ADR_"));
        if (samagraImg != null && !samagraImg.isEmpty()) student.setSamagraIdImage(saveFile(samagraImg, "SAM_"));
        if (passbookImg != null && !passbookImg.isEmpty()) student.setBankPassbookImage(saveFile(passbookImg, "BNK_"));
        if (apaarImg != null && !apaarImg.isEmpty()) student.setApaarCardImage(saveFile(apaarImg, "APR_"));
        if (birthCertificate != null && !birthCertificate.isEmpty()) student.setBirthCertificate(saveFile(birthCertificate, "BC_"));
        if (incomeCertificate != null && !incomeCertificate.isEmpty()) student.setIncomeCertificate(saveFile(incomeCertificate, "IC_"));
        if (castCertificate != null && !castCertificate.isEmpty()) student.setCastCertificate(saveFile(castCertificate, "CC_"));
        if (domicileCertificate != null && !domicileCertificate.isEmpty()) student.setDomicileCertificate(saveFile(domicileCertificate, "DC_"));


        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student promoteStudent(String enrollmentId, Long classId, Long sectionId, Long academicYearId) {
        Student student = studentRepository.findByEnrollmentIdAndIsActiveTrue(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Active Student not found with Enrollment ID: " + enrollmentId));

        SchoolClass newClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with ID: " + classId));
        student.setSchoolClass(newClass);

        if (sectionId != null && sectionId > 0) {
            Section newSection = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new RuntimeException("Section not found with ID: " + sectionId));
            student.setSection(newSection);
        } else {
            student.setSection(null);
        }

        if (academicYearId != null && academicYearId > 0) {
            AcademicYearConfig newYear = yearRepo.findById(academicYearId)
                    .orElseThrow(() -> new RuntimeException("Academic Year not found"));
            student.setAcademicYear(newYear);
        }

        return studentRepository.save(student);
    }

    @Override
    public List<Student> getFilteredStudents(Long schoolId, Long classId, Long sectionId, String gender, String caste, Long academicYearId) {
        return studentRepository.findByFilters(schoolId, classId, sectionId, gender, caste, academicYearId);
    }

    @Override
    public List<Student> getStudentsByCaste(Long schoolId, String caste, Long yearId) {
        return studentRepository.findBySchoolIdAndCasteAndAcademicYearIdAndIsActiveTrue(schoolId, caste, yearId);
    }

    @Override
    public List<Student> getStudentsByGender(Long schoolId, String gender, Long yearId) {
        return studentRepository.findBySchoolIdAndGenderAndAcademicYearIdAndIsActiveTrue(schoolId, gender, yearId);
    }

    @Override
    public List<Map<String, Object>> getCasteStatistics(Long schoolId, Long yearId) {
        return studentRepository.getCasteWiseCount(schoolId, yearId);
    }

    @Override
    public List<Map<String, Object>> getGenderStatistics(Long schoolId, Long yearId) {
        return studentRepository.getGenderWiseCount(schoolId, yearId);
    }

    @Override
    public Student getStudentByApaarId(String apaarId) {
        return studentRepository.findByApaarIdAndIsActiveTrue(apaarId).orElseThrow();
    }

    private String generateEnrollmentId() {
        String lastEnrollment = studentRepository.findLastEnrollmentId();
        int currentYear = java.time.Year.now().getValue();
        int nextId = 1;
        if (lastEnrollment != null && lastEnrollment.contains("-")) {
            try {
                String[] parts = lastEnrollment.split("-");
                nextId = Integer.parseInt(parts[parts.length - 1]) + 1;
            } catch (Exception e) { nextId = 1; }
        }
        return String.format("STU-%d-%03d", currentYear, nextId);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow();
        student.setActive(false);
        User user = student.getUser();
        if (user != null) {
            user.setActive(false);
            user.setUsername("deleted_" + UUID.randomUUID().toString().substring(0, 8) + "_" + user.getUsername());
            userRepository.save(user);
        }
        student.setEmail("deleted_" + System.currentTimeMillis() + "_" + student.getEmail());
        studentRepository.save(student);
    }

    @Override public List<Student> getStudentsBySchool(Long schoolId) { return studentRepository.findBySchoolIdAndIsActiveTrue(schoolId); }
    @Override public List<Student> getStudentsByClass(Long schoolId, Long classId, Long yearId) { return studentRepository.findBySchoolIdAndSchoolClassIdAndAcademicYearIdAndIsActiveTrue(schoolId, classId, yearId); }
    @Override public List<Student> getStudentsByClassAndSection(Long schoolId, Long classId, Long sectionId, Long yearId) { return studentRepository.findBySchoolIdAndSchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(schoolId, classId, sectionId, yearId); }

    @Override public Student getStudentByRollNo(String enrollmentId) {
        return studentRepository.findByEnrollmentIdAndIsActiveTrue(enrollmentId).orElseThrow();
    }

    @Override public Student getStudentById(Long id) { return studentRepository.findById(id).filter(Student::isActive).orElseThrow(); }

    @Override
    @Transactional
    public Student updateStudent(Long id, Student details) {
        Student student = getStudentById(id);
        student.setName(details.getName());
        student.setPhoneNo(details.getPhoneNo());
        student.setAddress(details.getAddress());
        student.setGender(details.getGender());
        student.setCaste(details.getCaste());
        student.setApaarId(details.getApaarId());
        if(details.getRollNumber() != null) student.setRollNumber(details.getRollNumber());
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student updateStudentByRollNo(String enrollmentId, Student details, Long newClassId, Long newSectionId) {
        Student student = studentRepository.findByEnrollmentIdAndIsActiveTrue(enrollmentId).orElseThrow();
        student.setName(details.getName());
        student.setGender(details.getGender());
        student.setCaste(details.getCaste());
        if (newClassId != null && newClassId > 0) student.setSchoolClass(classRepository.findById(newClassId).orElseThrow());
        if (newSectionId != null && newSectionId > 0) student.setSection(sectionRepository.findById(newSectionId).orElseThrow());
        if(details.getRollNumber() != null) student.setRollNumber(details.getRollNumber());
        return studentRepository.save(student);
    }

    private String saveFile(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String fileName = prefix + System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Ensure upload directory exists
        Path dirPath = Paths.get(getStudentUploadDir());
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Path path = Paths.get(getStudentUploadDir() + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
}