package com.smartschool.api.service;

import com.smartschool.api.entity.Attendance;
import com.smartschool.api.entity.Student;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    String markBulkAttendance(Long schoolId, Long classId, Long sectionId,
                              Long teacherId, List<Attendance> attendanceList);

    Attendance markSingleStudentAttendance(Long schoolId, Long classId,
                                           Long sectionId, Long teacherId,
                                           Attendance attendance);

    List<Attendance> getAttendanceByDate(Long schoolId, Long classId,
                                         Long sectionId, LocalDate date);

    List<Attendance> getAllAttendanceByStudentId(Long studentId);

    Double calculateAttendancePercentage(Long studentId);

    Attendance updateAttendanceStatus(Long attendanceId, String status);

    Attendance updateByRollNumber(String rollNo, LocalDate date, String status);

    void deleteAttendanceRecord(Long classId, Long sectionId, LocalDate date);

    void deleteByStudentId(Long studentId, LocalDate date);

    void deleteByRollNumber(String rollNo, LocalDate date);

    byte[] generateAttendanceExcel(Long schoolId, Long classId, Long sectionId);

    List<Student> getStudentsForTeacher(Long teacherId, Long schoolId);

    // ✅ FIX: String academicYear → Long yearId
    List<Attendance> getAttendanceForReport(Long schoolId, Long classId,
                                            Long sectionId, Long yearId);

    // ✅ FIX: String academicYear → Long yearId
    void deleteExportedData(Long schoolId, Long classId,
                            Long sectionId, Long yearId);
}