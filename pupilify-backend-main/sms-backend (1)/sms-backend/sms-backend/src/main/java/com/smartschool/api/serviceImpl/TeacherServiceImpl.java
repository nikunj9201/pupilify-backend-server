package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.TeacherService;
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

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Naye Repositories add kiye Cascade Inactivation ke liye
    @Autowired
    private ClassTeacherRepository mappingRepo;
    @Autowired
    private DailyTimeTableRepository timeTableRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private String getTeacherUploadDir() {
        return uploadDir + "/teachers/";
    }

    @Override
    @Transactional
    public Teacher onboardTeacher(Teacher teacher, MultipartFile photo, MultipartFile aadharImg, MultipartFile passbookImg, Long schoolId) throws IOException {

        // ✅ LOGIC: Check if an ACTIVE teacher exists [cite: 271]
        userRepository.findByUsername(teacher.getMail()).ifPresent(u -> {
            if (u.isActive()) {
                throw new RuntimeException("Error: This teacher email is already active!");
            }
        });

        File directory = new File(getTeacherUploadDir());
        if (!directory.exists()) directory.mkdirs();

        teacher.setTeacherPhoto(saveFile(photo, "PHOTO_"));
        teacher.setAadharImage(saveFile(aadharImg, "AADHAR_"));
        teacher.setBankPassbookImage(saveFile(passbookImg, "BANK_"));

        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new RuntimeException("School not found"));

        // ✅ User Setup (Fresh Account) [cite: 274-275]
        User user = new User();
        user.setUsername(teacher.getMail());
        user.setPassword(passwordEncoder.encode(teacher.getPassword()));
        user.setRole(Role.ROLE_TEACHER);
        user.setSchool(school);
        user.setActive(true);

        teacher.setUser(user);
        teacher.setSchool(school);
        teacher.setActive(true); // Default active on creation
        return teacherRepository.save(teacher);
    }

    // ✅ UPDATED DELETE LOGIC: Soft Delete + Email Renaming + Cascade Assignments Clear
    @Override
    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // 1. Mark teacher as Inactive [cite: 277]
        teacher.setActive(false);

        // 2. Disable login & Rename to free the email [cite: 278-281]
        User user = teacher.getUser();
        if (user != null) {
            user.setActive(false); // Stop login access
            user.setUsername("deleted_" + System.currentTimeMillis() + "_" + user.getUsername());
            userRepository.save(user);
        }

        teacher.setMail("deleted_" + System.currentTimeMillis() + "_" + teacher.getMail());

        // ─────────────────────────────────────────────────────────
        // ✅ NAYA LOGIC: AUTOMATIC ASSIGNMENT REMOVAL
        // ─────────────────────────────────────────────────────────

        // A. Class Teacher Mapping ko inactive karna
        // Isse Principal ko dashboard par ye class vacant (khali) dikhegi
        List<ClassTeacherMapping> activeMappings = mappingRepo.findByTeacherIdAndIsActiveTrue(id);
        activeMappings.forEach(mapping -> mapping.setActive(false));
        mappingRepo.saveAll(activeMappings);

        // B. Daily Timetable se periods ko inactive karna
        // Teacher system se bahar hai, isliye periods khali ho jane chahiye
        List<DailyTimeTable> activeSlots = timeTableRepo.findByTeacherIdAndIsActiveTrue(id);
        activeSlots.forEach(slot -> slot.setActive(false));
        timeTableRepo.saveAll(activeSlots);

        teacherRepository.save(teacher);
    }

    // --- Baki methods ---
    @Override
    public List<Teacher> getTeachersBySchool(Long schoolId) {
        return teacherRepository.findBySchoolId(schoolId);
    }

    @Override
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + id));
    }

    @Override
    @Transactional
    public Teacher updateTeacher(Long id, Teacher details) {
        Teacher teacher = getTeacherById(id);
        teacher.setName(details.getName());
        teacher.setAddress(details.getAddress());
        teacher.setQualification(details.getQualification());
        teacher.setSubjectExpertise(details.getSubjectExpertise());
        teacher.setSalary(details.getSalary());
        teacher.setPhoneNumber(details.getPhoneNumber());
        teacher.setAlternateNumber(details.getAlternateNumber());
        teacher.setAadharNo(details.getAadharNo());
        teacher.setDob(details.getDob());
        return teacherRepository.save(teacher);
    }

    @Override
    public List<ClassTeacherMapping> getActiveMappingsForTeacher(Long teacherId) {
        // ✅ MappingRepository ka use karke sirf wahi data nikalna jo active (0x01) hai
        // table ke hisab se ye filter lagana zaroori hai
        return mappingRepo.findByTeacherIdAndIsActiveTrue(teacherId);
    }

    private String saveFile(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String fileName = prefix + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(getTeacherUploadDir() + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    // ... aapke purane saare methods (onboard, delete, update, etc.) yahan rahenge ...

    @Override
    public byte[] getTeacherDocument(Long id, String docType) {
        // 1. Teacher find karein
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + id));

        String fileName = null;

        // 2. docType ke hisab se sahi String path (fileName) uthayein
        switch (docType.toLowerCase()) {
            case "photo":
                fileName = teacher.getTeacherPhoto();
                break;
            case "aadharimg":
                fileName = teacher.getAadharImage();
                break;
            case "passbookimg":
                fileName = teacher.getBankPassbookImage();
                break;
            default:
                throw new RuntimeException("Invalid document type: " + docType);
        }

        // 3. Check karein ki DB mein file name hai ya nahi
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("Document '" + docType + "' not found for this teacher.");
        }

        try {
            // 4. UPLOAD_DIR (uploads/teachers/) se file read karein
            Path filePath = Paths.get(getTeacherUploadDir()).resolve(fileName);

            if (Files.exists(filePath)) {
                return Files.readAllBytes(filePath);
            } else {
                throw new RuntimeException("File not found on server disk: " + fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage());
        }
    }

    // saveFile method aur class ki ending curly bracket yahan rahegi...


}