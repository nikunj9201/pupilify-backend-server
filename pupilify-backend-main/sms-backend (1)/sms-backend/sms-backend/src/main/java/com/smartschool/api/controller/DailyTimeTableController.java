package com.smartschool.api.controller;

import com.smartschool.api.entity.DailyTimeTable;
import com.smartschool.api.service.DailyTimeTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/timetable")
@CrossOrigin("*")
public class DailyTimeTableController {

    private static final Logger log = LoggerFactory.getLogger(DailyTimeTableController.class);

    @Autowired private DailyTimeTableService timeTableService;

    @PostMapping("/assign/{schoolId}/{classId}/{sectionId}")
    public ResponseEntity<String> assignSlot(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @PathVariable Long sectionId,
            @RequestBody DailyTimeTable dailyTimeTable) {

        log.info("Assigning timetable slot: schoolId={} classId={} sectionId={}",
                schoolId, classId, sectionId);

        if (sectionId == 0) {
            dailyTimeTable.setSection(null);
        }

        String result = timeTableService.saveTimeTableSlot(dailyTimeTable);

        if (result.startsWith("Error")) {
            log.warn("Timetable slot assignment FAILED: {}", result);
            return ResponseEntity.badRequest().body(result);
        }

        log.info("Timetable slot assigned successfully.");
        return ResponseEntity.ok(result);
    }

    /**
     * ✅ FIX: @RequestParam String academicYear → @RequestParam Long academicYearId
     * DailyTimeTableService.getTeacherScheduleByDay() ab Long yearId leta hai
     *
     * API: GET /api/admin/timetable/teacher-schedule/{teacherId}?day=MONDAY&academicYearId=1
     */
    @GetMapping("/teacher-schedule/{teacherId}")
    public ResponseEntity<List<DailyTimeTable>> getTeacherSchedule(
            @PathVariable Long teacherId,
            @RequestParam String day,
            @RequestParam Long academicYearId) {  // ✅ String → Long

        log.debug("Fetching schedule for teacherId:{} day:{} yearId:{}",
                teacherId, day, academicYearId);

        return ResponseEntity.ok(
                timeTableService.getTeacherScheduleByDay(teacherId, day, academicYearId));
    }

    /**
     * ✅ FIX: @RequestParam String academicYear → @RequestParam Long academicYearId
     * DailyTimeTableService.getClassSchedule() ab Long yearId leta hai
     *
     * API: GET /api/admin/timetable/view/{schoolId}/{classId}/{sectionId}?academicYearId=1
     */
    @GetMapping("/view/{schoolId}/{classId}/{sectionId}")
    public ResponseEntity<List<DailyTimeTable>> getView(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @PathVariable Long sectionId,
            @RequestParam Long academicYearId) {  // ✅ String → Long

        log.debug("Fetching timetable view: schoolId={} classId={} sectionId={} yearId={}",
                schoolId, classId, sectionId, academicYearId);

        Long finalSecId = (sectionId > 0) ? sectionId : null;

        return ResponseEntity.ok(
                timeTableService.getClassSchedule(schoolId, classId, finalSecId, academicYearId));
    }

    @PutMapping("/delete-slot/{id}")
    public ResponseEntity<String> deleteSlot(@PathVariable Long id) {
        log.warn("Inactivating timetable slot ID: {}", id);
        try {
            timeTableService.inactivateSlot(id);
            log.info("Slot ID:{} inactivated.", id);
            return ResponseEntity.ok("Period deleted (inactivated) successfully!");
        } catch (Exception e) {
            log.error("Slot inactivation FAILED for ID:{} | Error: {}",
                    id, e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}