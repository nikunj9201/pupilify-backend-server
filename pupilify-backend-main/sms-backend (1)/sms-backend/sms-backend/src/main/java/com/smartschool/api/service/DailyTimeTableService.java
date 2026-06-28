package com.smartschool.api.service;

import com.smartschool.api.entity.DailyTimeTable;
import com.smartschool.api.repository.DailyTimeTableRepository;
import com.smartschool.api.repository.ClassTeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DailyTimeTableService {

    @Autowired private DailyTimeTableRepository timeTableRepo;
    @Autowired private ClassTeacherRepository mappingRepo;

    public String saveTimeTableSlot(DailyTimeTable tt) {
        // ✅ FIX: yearId Long se pass karo
        Long yearId = tt.getAcademicYear() != null
                ? tt.getAcademicYear().getId() : null;
        if (yearId == null) {
            return "Error: Academic Year set nahi hai!";
        }

        boolean busy = timeTableRepo.isTeacherBusy(
                tt.getTeacher().getId(),
                tt.getDayOfWeek(),
                tt.getStartTime(),
                yearId);
        if (busy) {
            return "Error: Teacher already assigned at this time!";
        }

        boolean isClassTeacher = mappingRepo
                .existsBySchoolIdAndSchoolClassIdAndTeacherIdAndIsActiveTrue(
                        tt.getSchool().getId(),
                        tt.getSchoolClass().getId(),
                        tt.getTeacher().getId());

        tt.setActive(true);
        timeTableRepo.save(tt);
        return isClassTeacher
                ? "Success: Class Teacher Period saved!"
                : "Success: Slot saved!";
    }

    // ✅ FIX: String year ki jagah yearId (Long)
    public List<DailyTimeTable> getClassSchedule(
            Long schoolId, Long classId,
            Long sectionId, Long yearId) {
        return timeTableRepo.findActiveSchedule(
                schoolId, classId, sectionId, yearId);
    }

    // ✅ FIX: String year ki jagah yearId (Long)
    public List<DailyTimeTable> getTeacherScheduleByDay(
            Long teacherId, String day, Long yearId) {
        return timeTableRepo.findTeacherDaySchedule(
                teacherId, day, yearId);
    }

    public void inactivateSlot(Long id) {
        DailyTimeTable tt = timeTableRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Slot not found: " + id));
        tt.setActive(false);
        timeTableRepo.save(tt);
    }

    public void handleCascadeInactivation(
            Long classId, Long sectionId, Long schoolId) {
        if (sectionId != null && sectionId > 0) {
            timeTableRepo.deactivateBySection(sectionId, schoolId);
        } else {
            timeTableRepo.deactivateByClass(classId, schoolId);
        }
    }
}
