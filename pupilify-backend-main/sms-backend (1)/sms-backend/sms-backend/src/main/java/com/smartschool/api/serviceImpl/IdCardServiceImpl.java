package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.IdCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@Service
public class IdCardServiceImpl implements IdCardService {

    private static final Logger log = LoggerFactory.getLogger(IdCardServiceImpl.class);

    @Autowired private StudentRepository studentRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private SchoolRepository  schoolRepository;
    @Autowired private AcademicYearConfigRepository academicYearRepo;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    // ─────────────────────────────────────────────────────────────────────────
    // 1. STUDENT ID CARD (Updated for Enrollment ID)
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public byte[] generateStudentIdCard(String enrollmentId, Long schoolId) {
        log.info("Generating Student ID card: enrollmentId={} schoolId={}", enrollmentId, schoolId);

        // 🚩 Find by Enrollment ID instead of numeric ID
        Student student = studentRepository.findByEnrollmentIdAndIsActiveTrue(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + enrollmentId));

        if (!student.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Student belongs to a different school.");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found: " + schoolId));

        String html = buildStudentCardHtml(student, school);
        return html.getBytes(StandardCharsets.UTF_8);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. TEACHER ID CARD (As-is)
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public byte[] generateTeacherIdCard(Long teacherId, Long schoolId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found: " + teacherId));

        if (!teacher.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Teacher belongs to a different school.");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found: " + schoolId));

        String html = buildTeacherCardHtml(teacher, school);
        return html.getBytes(StandardCharsets.UTF_8);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. BULK STUDENT ID CARDS (As-is)
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public byte[] generateBulkStudentIdCards(Long schoolId, Long classId, Long sectionId) {
        AcademicYearConfig currentYear = academicYearRepo
                .findTopBySchoolIdOrderByIdDesc(schoolId)
                .orElseThrow(() -> new RuntimeException(
                        "School ID " + schoolId + " ka koi active Academic Year nahi mila."));

        Long yearId = currentYear.getId();
        List<Student> students;

        if (sectionId != null && sectionId > 0) {
            students = studentRepository
                    .findBySchoolIdAndSchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(
                            schoolId, classId, sectionId, yearId);
        } else {
            students = studentRepository
                    .findBySchoolIdAndSchoolClassIdAndAcademicYearIdAndIsActiveTrue(
                            schoolId, classId, yearId);
        }

        if (students.isEmpty()) {
            throw new RuntimeException("Koi students nahi mile.");
        }

        School school = schoolRepository.findById(schoolId).orElseThrow();
        StringBuilder allCards = new StringBuilder();
        allCards.append(getBulkPageHeader());

        for (int i = 0; i < students.size(); i++) {
            allCards.append(buildStudentCardBody(students.get(i), school));
            if (i % 2 == 1) {
                allCards.append("<div style='page-break-after:always;'></div>");
            }
        }

        allCards.append(getBulkPageFooter());
        return allCards.toString().getBytes(StandardCharsets.UTF_8);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HTML BUILDERS
    // ─────────────────────────────────────────────────────────────────────────

    private String buildStudentCardHtml(Student s, School school) {
        return getFullPageWrapper(
                buildStudentCardBody(s, school),
                "Student ID Card - " + s.getName());
    }

    private String buildTeacherCardHtml(Teacher t, School school) {
        return getFullPageWrapper(
                buildTeacherCardBody(t, school),
                "Teacher ID Card - " + t.getName());
    }

    private String buildStudentCardBody(Student s, School school) {
        String photoBase64      = getPhotoBase64(s.getStudentPhoto(), "student");
        String schoolLogoBase64 = getSchoolLogoBase64(school.getSchoolLogo());
        String className        = s.getSchoolClass() != null
                ? s.getSchoolClass().getClassName() : "N/A";
        String sectionName      = s.getSection() != null
                ? " - " + s.getSection().getSectionName() : "";
        String yearString       = (s.getAcademicYear() != null)
                ? s.getAcademicYear().getCurrentYear()
                : school.getCurrentYear() != null
                ? school.getCurrentYear().getCurrentYear()
                : "N/A";

        // 🚩 Enrollment ID badge ke liye aur Roll Number Table Row ke liye
        String enrollmentId = safe(s.getEnrollmentId(), "N/A");
        String rollNumber   = s.getRollNumber() != null ? String.valueOf(s.getRollNumber()) : "N/A";

        return """
            <div class="id-card student-card">
              <div class="card-header student-header">
                <div class="logo-section">%s</div>
                <div class="school-info">
                  <div class="school-name">%s</div>
                  <div class="school-address">%s</div>
                </div>
                <div class="card-type-badge">STUDENT</div>
              </div>
              <div class="card-body">
                <div class="photo-section">
                  <div class="photo-wrapper">%s</div>
                  <div class="roll-badge">ID: %s</div>
                </div>
                <div class="info-section">
                  <div class="student-name">%s</div>
                  <table class="info-table">
                    <tr><td class="label">Roll No</td><td class="value">%s</td></tr>
                    <tr><td class="label">Class</td><td class="value">%s%s</td></tr>
                    <tr><td class="label">Father</td><td class="value">%s</td></tr>
                    <tr><td class="label">DOB</td><td class="value">%s</td></tr>
                    <tr><td class="label">Gender</td><td class="value">%s</td></tr>
                    <tr><td class="label">Contact</td><td class="value">%s</td></tr>
                  </table>
                </div>
              </div>
              <div class="card-footer student-footer">
                <div class="footer-left"><div class="addr-text">%s</div></div>
              </div>
              <div class="validity-bar">Valid for Academic Year: %s</div>
            </div>
            """.formatted(
                schoolLogoBase64,
                safe(school.getSchoolName(), "School"),
                safe(school.getAddress(), ""),
                photoBase64,
                enrollmentId,            // Photo ke neeche ID (Enrollment ID)
                safe(s.getName(), ""),
                rollNumber,              // Table mein manual Roll Number
                className, sectionName,
                safe(s.getFatherName(), "N/A"),
                safe(s.getDob(), "N/A"),
                safe(s.getGender(), "N/A"),
                safe(s.getPhoneNo(), "N/A"),
                safe(school.getAddress(), ""),
                yearString
        );
    }

    private String buildTeacherCardBody(Teacher t, School school) {
        String photoBase64      = getPhotoBase64(t.getTeacherPhoto(), "teacher");
        String schoolLogoBase64 = getSchoolLogoBase64(school.getSchoolLogo());
        String teacherCode      = "TCH-" + school.getId() + "-" + t.getId();

        return """
            <div class="id-card teacher-card">
              <div class="card-header teacher-header">
                <div class="logo-section">%s</div>
                <div class="school-info">
                  <div class="school-name">%s</div>
                  <div class="school-address">%s</div>
                </div>
                <div class="card-type-badge">STAFF</div>
              </div>
              <div class="card-body">
                <div class="photo-section">
                  <div class="photo-wrapper">%s</div>
                  <div class="roll-badge">%s</div>
                </div>
                <div class="info-section">
                  <div class="student-name">%s</div>
                  <table class="info-table">
                    <tr><td class="label">Expertise</td><td class="value">%s</td></tr>
                    <tr><td class="label">Contact</td><td class="value">%s</td></tr>
                  </table>
                </div>
              </div>
              <div class="card-footer teacher-footer">
                <div class="footer-left"><div class="addr-text">%s</div></div>
              </div>
            </div>
            """.formatted(
                schoolLogoBase64,
                safe(school.getSchoolName(), "School"),
                safe(school.getAddress(), ""),
                photoBase64,
                teacherCode,
                safe(t.getName(), ""),
                safe(t.getSubjectExpertise(), "N/A"),
                safe(t.getPhoneNumber(), "N/A"),
                safe(school.getAddress(), "")
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS & CSS (As-is)
    // ─────────────────────────────────────────────────────────────────────────

    private String getSchoolLogoBase64(String fileName) {
        if (fileName == null || fileName.isBlank()) return "🏫";
        try {
            java.io.File file = new java.io.File(uploadDir + "schools/" + fileName);
            if (file.exists()) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                return "<img src='data:image/jpeg;base64,"
                        + Base64.getEncoder().encodeToString(bytes)
                        + "' style='height:45px; width:45px;"
                        + " border-radius:50%; object-fit:cover; border: 2px solid white;' />";
            }
        } catch (Exception e) {
            log.warn("School Logo error: {}", e.getMessage());
        }
        return "🏫";
    }

    private String getFullPageWrapper(String cardHtml, String title) {
        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"><title>%s</title>%s</head>
            <body>
              <div class="print-container">%s</div>
              <div style="text-align:center; margin-top:30px;" class="no-print">
                <button class="print-btn" onclick="window.print()">
                  Print / Save PDF
                </button>
              </div>
            </body>
            </html>
            """.formatted(title, getCommonCss(), cardHtml);
    }

    private String getCommonCss() {
        return """
            <style>
              body { font-family: 'Roboto', sans-serif; padding: 20px; background: #f4f4f4; }
              .print-container { display: flex; flex-wrap: wrap; gap: 20px; justify-content: center; }
              .id-card { width: 340px; border-radius: 12px; overflow: hidden; background: white;
                         box-shadow: 0 4px 10px rgba(0,0,0,0.1); border: 1px solid #eee; position: relative; }
              .card-header { padding: 12px; color: white; display: flex; align-items: center; gap: 15px; }
              .student-header { background: linear-gradient(135deg, #1565c0, #1e88e5); }
              .teacher-header  { background: linear-gradient(135deg, #2e7d32, #43a047); }
              
              .logo-section { flex-shrink: 0; display: flex; align-items: center; justify-content: center; }
              .school-info { flex-grow: 1; text-align: left; }
              .school-name { font-size: 15px; font-weight: bold; text-transform: uppercase; margin: 0; }
              .school-address { font-size: 10px; opacity: 0.9; margin: 2px 0 0 0; }
              
              .card-type-badge { font-size: 9px; border: 1px solid white; padding: 2px 6px;
                                 border-radius: 4px; font-weight: bold; position: absolute; top: 12px; right: 12px; }
              .card-body { display: flex; padding: 15px; gap: 15px; }
              .photo-wrapper { width: 90px; height: 110px; border: 2px solid #f0f0f0;
                               border-radius: 6px; overflow: hidden; background: #fafafa; }
              .photo-wrapper img { width: 100%; height: 100%; object-fit: cover; }
              .roll-badge { background: #333; color: white; font-size: 9px; text-align: center;
                            margin-top: 5px; border-radius: 4px; padding: 2px; min-width: 80px; }
              .student-name { font-size: 16px; font-weight: bold; margin-bottom: 8px; color: #1565c0; text-transform: capitalize; }
              .info-table { width: 100%; border-collapse: collapse; }
              .info-table td { font-size: 11px; padding: 4px 0; color: #444; }
              .info-table .label { font-weight: bold; color: #888; width: 65px; }
              .card-footer { padding: 10px 15px; background: #fdfdfd; border-top: 1px dashed #ddd; }
              .addr-text { font-size: 9px; color: #666; text-align: center; font-weight: 500; }
              .validity-bar { background: #333; color: white; text-align: center;
                              font-size: 10px; padding: 6px; font-weight: 500; }
              .print-btn { padding: 12px 30px; background: #1565c0; color: white; border: none; 
                           border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 14px;
                           box-shadow: 0 4px 10px rgba(0,0,0,0.2); transition: 0.3s; }
              .print-btn:hover { background: #0d47a1; }
              @media print { 
                .no-print, .print-btn { display: none !important; } 
                body { background: white; padding: 0; } 
                .id-card { box-shadow: none; border: 1px solid #ccc; }
              }
            </style>
            """;
    }

    private String getBulkPageHeader() {
        return "<!DOCTYPE html><html><head>"
                + getCommonCss()
                + "</head><body><div class='print-container'>";
    }

    private String getBulkPageFooter() {
        return "</div><div style='text-align:center; margin-top:30px;' class='no-print'>"
                + "<button class='print-btn' onclick='window.print()'>Print All Cards</button></div></body></html>";
    }

    private String getPhotoBase64(String fileName, String type) {
        if (fileName == null || fileName.isBlank()) return "👤";
        try {
            java.io.File file = new java.io.File(
                    uploadDir + (type.equals("student") ? "students/" : "teachers/") + fileName);
            if (file.exists()) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                return "<img src='data:image/jpeg;base64,"
                        + Base64.getEncoder().encodeToString(bytes) + "' />";
            }
        } catch (Exception e) {
            log.warn("Photo error: {}", e.getMessage());
        }
        return "👤";
    }

    private String safe(String v, String fallback) {
        return (v != null && !v.isBlank()) ? v : fallback;
    }
}