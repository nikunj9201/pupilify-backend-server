package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.time.ZoneId;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private SchoolRepository schoolRepository;
    @Autowired private ClassTeacherRepository mappingRepository;
    @Autowired private AcademicYearConfigRepository academicYearRepo;

    @Autowired private ClassRepository classRepository;
    @Autowired private SectionRepository sectionRepository;

    @Override
    @Transactional
    public String markBulkAttendance(Long schoolId, Long classId, Long sectionId,
                                     Long teacherId, List<Attendance> attendanceList) {

        log.info("Marking bulk attendance: schoolId={} classId={} sectionId={} teacherId={} count={}",
                schoolId, classId, sectionId, teacherId, attendanceList.size());

        Long finalSectionId = (sectionId != null && sectionId > 0) ? sectionId : null;

        AcademicYearConfig currentConfig = academicYearRepo
                .findTopBySchoolIdOrderByIdDesc(schoolId)
                .orElseThrow(() -> new RuntimeException(
                        "School ID " + schoolId + " ka koi Academic Year config nahi mila!"));

        Long currentYearId = currentConfig.getId();

        boolean isAuthorized = attendanceRepository.isAuthorizedTeacher(
                teacherId,
                classId,
                finalSectionId,
                currentYearId);

        if (!isAuthorized) {
            log.warn("UNAUTHORIZED attendance attempt by teacherId:{} for classId:{}", teacherId, classId);
            throw new RuntimeException("Aap is class ke authorized teacher nahi hain!");
        }

        School school = schoolRepository.findById(schoolId).orElseThrow();
        SchoolClass schoolClass = classRepository.findById(classId).orElse(null);
        Section section = (finalSectionId != null) ? sectionRepository.findById(finalSectionId).orElse(null) : null;

        for (Attendance att : attendanceList) {
            String rollNo = att.getRollNumber(); // 🚩 Ye enrollment ID (STU-...) hai

            if (rollNo == null) {
                log.warn("Skipping record: Roll Number is null");
                continue;
            }

            log.debug("Processing attendance for rollNo: {} status: {}", rollNo, att.getStatus());

            // 🚩 FIX: findByRollNumber ki jagah findByEnrollmentId use kiya kyunki rollNo String hai
            Student student = studentRepository.findByEnrollmentIdAndIsActiveTrue(rollNo)
                    .orElse(null);

            if (student == null) {
                log.warn("Student NOT FOUND for rollNo:{} — skipping", rollNo);
                continue;
            }

            attendanceRepository.deleteByStudentIdAndDate(student.getId(), LocalDate.now());

            att.setStudent(student);
            att.setSchool(school);
            att.setSchoolClass(schoolClass);
            att.setSection(section);
            att.setAttendanceDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
            att.setAcademicYear(currentConfig);

            attendanceRepository.save(att);
        }

        log.info("Bulk attendance marked successfully for {} records.", attendanceList.size());
        return "Bulk attendance marked successfully.";
    }

    @Override
    public List<Attendance> getAttendanceByDate(Long schoolId, Long classId,
                                                Long sectionId, LocalDate date) {
        log.debug("Fetching attendance for date={} schoolId={} classId={} sectionId={}",
                date, schoolId, classId, sectionId);
        return attendanceRepository.findReport(schoolId, classId, sectionId, date);
    }

    @Override
    public Double calculateAttendancePercentage(Long studentId) {
        log.debug("Calculating attendance % for studentId: {}", studentId);
        List<Attendance> records = attendanceRepository.findByStudentId(studentId);
        if (records.isEmpty()) {
            log.warn("No attendance records found for studentId: {}", studentId);
            return 0.0;
        }
        long present = records.stream()
                .filter(a -> "Present".equalsIgnoreCase(a.getStatus()))
                .count();
        double percentage = (double) (present * 100) / records.size();
        double result = Math.round(percentage * 100.0) / 100.0;
        return result;
    }

    @Override
    public List<Student> getStudentsForTeacher(Long teacherId, Long schoolId) {
        ClassTeacherMapping mapping = mappingRepository.findByTeacherIdAndIsActiveTrue(teacherId)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Aap kisi bhi class ke active teacher nahi hain!"));

        Long classId = mapping.getSchoolClass().getId();
        Long sectionId = (mapping.getSection() != null) ? mapping.getSection().getId() : null;
        return studentRepository.findActiveStudentsForTeacher(schoolId, classId, sectionId);
    }

    @Override
    public List<Attendance> getAttendanceForReport(Long schoolId, Long classId,
                                                   Long sectionId, Long yearId) {
        return attendanceRepository.findBySchoolClassSectionYear(
                schoolId, classId,
                (sectionId != null && sectionId > 0) ? sectionId : null,
                yearId);
    }

    @Override
    @Transactional
    public void deleteExportedData(Long schoolId, Long classId,
                                   Long sectionId, Long yearId) {
        attendanceRepository.deleteBySchoolIdAndAcademicYearId(schoolId, yearId);
    }

    @Override
    @Transactional
    public void deleteByStudentId(Long id, LocalDate d) {
        attendanceRepository.deleteByStudentIdAndDate(id, d);
    }

    @Override
    @Transactional
    public void deleteByRollNumber(String rollNo, LocalDate date) {
        // 🚩 Enrollment ID se search kiya taaki Integer conflict na ho
        studentRepository.findByEnrollmentIdAndIsActiveTrue(rollNo).ifPresent(student ->
                attendanceRepository.deleteByStudentIdAndDate(student.getId(), date)
        );
    }

    @Override
    public List<Attendance> getAllAttendanceByStudentId(Long id) {
        return attendanceRepository.findByStudentId(id);
    }

    @Override
    @Transactional
    public Attendance updateAttendanceStatus(Long id, String status) {
        Attendance a = attendanceRepository.findById(id).orElseThrow();
        a.setStatus(status);
        return attendanceRepository.save(a);
    }

    @Override public byte[] generateAttendanceExcel(Long sid, Long cid, Long secid) { return new byte[0]; }
    @Override public Attendance markSingleStudentAttendance(Long s, Long c, Long sec, Long t, Attendance a) { return null; }
    @Override public Attendance updateByRollNumber(String r, LocalDate d, String s) { return null; }
    @Override public void deleteAttendanceRecord(Long c, Long s, LocalDate d) {}
}