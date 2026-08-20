package com.smartschool.api.controller;

import com.smartschool.api.dto.BusFeeRateStructureDTO;
import com.smartschool.api.dto.StudentMonthlyFeeStructureDTO;
import com.smartschool.api.dto.StudentBusAssignmentRequestDTO;
import com.smartschool.api.entity.BusFeeRateStructure;
import com.smartschool.api.entity.StudentMonthlyFeeStructure;
import com.smartschool.api.service.BusMonthlyFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bus-monthly-fees")
@CrossOrigin("*")
public class BusMonthlyFeeController {

    @Autowired
    private BusMonthlyFeeService busMonthlyFeeService;

    // Fee Rate Structure Endpoints
    @PostMapping("/rate-structure/create")
    public ResponseEntity<BusFeeRateStructure> createFeeRateStructure(
            @RequestParam Long stoppageId,
            @RequestParam Long busId,
            @RequestParam Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam double monthlyFeeAmount,
            @RequestParam(defaultValue = "12") int totalMonths,
            @RequestParam(defaultValue = "July") String academicYearStartMonth) {
        return ResponseEntity.ok(busMonthlyFeeService.createFeeRateStructure(stoppageId, busId, schoolId, academicYearId, monthlyFeeAmount, totalMonths, academicYearStartMonth));
    }

    @PutMapping("/rate-structure/{rateStructureId}")
    public ResponseEntity<BusFeeRateStructure> updateFeeRateStructure(
            @PathVariable Long rateStructureId,
            @RequestParam double monthlyFeeAmount) {
        return ResponseEntity.ok(busMonthlyFeeService.updateFeeRateStructure(rateStructureId, monthlyFeeAmount));
    }

    @GetMapping("/rate-structure/bus/{busId}")
    public ResponseEntity<List<BusFeeRateStructureDTO>> getFeeRateStructuresByBus(
            @PathVariable Long busId,
            @RequestParam Long schoolId,
            @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busMonthlyFeeService.getFeeRateStructuresByBus(busId, schoolId, academicYearId));
    }

    @DeleteMapping("/rate-structure/{rateStructureId}")
    public ResponseEntity<Void> deleteFeeRateStructure(@PathVariable Long rateStructureId) {
        busMonthlyFeeService.deleteFeeRateStructure(rateStructureId);
        return ResponseEntity.ok().build();
    }

    // Student Monthly Fee Assignment Endpoints
    @PostMapping("/assign-student")
    public ResponseEntity<StudentMonthlyFeeStructure> assignStudentMonthlyFees(
            @RequestBody StudentBusAssignmentRequestDTO request) {
        return ResponseEntity.ok(busMonthlyFeeService.assignStudentMonthlyFees(request));
    }

    @PutMapping("/assignment/{assignmentId}")
    public ResponseEntity<StudentMonthlyFeeStructure> updateStudentFeeAssignment(
            @PathVariable Long assignmentId,
            @RequestParam List<String> selectedMonths,
            @RequestParam String paymentFrequency) {
        return ResponseEntity.ok(busMonthlyFeeService.updateStudentFeeAssignment(assignmentId, selectedMonths, paymentFrequency));
    }

    @GetMapping("/assignment/{studentId}")
    public ResponseEntity<StudentMonthlyFeeStructureDTO> getStudentFeeAssignment(
            @PathVariable Long studentId,
            @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busMonthlyFeeService.getStudentFeeAssignment(studentId, academicYearId));
    }

    @GetMapping("/assignments/bus/{busId}")
    public ResponseEntity<List<StudentMonthlyFeeStructureDTO>> getStudentFeeAssignmentsByBus(
            @PathVariable Long busId,
            @RequestParam Long schoolId,
            @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busMonthlyFeeService.getStudentFeeAssignmentsByBus(busId, schoolId, academicYearId));
    }

    @GetMapping("/assignments/stoppage/{stoppageId}")
    public ResponseEntity<List<StudentMonthlyFeeStructureDTO>> getStudentFeeAssignmentsByStoppage(
            @PathVariable Long stoppageId,
            @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busMonthlyFeeService.getStudentFeeAssignmentsByStoppage(stoppageId, academicYearId));
    }

    @DeleteMapping("/assignment/{assignmentId}")
    public ResponseEntity<Void> deleteStudentFeeAssignment(@PathVariable Long assignmentId) {
        busMonthlyFeeService.deleteStudentFeeAssignment(assignmentId);
        return ResponseEntity.ok().build();
    }

    // Reports
    @GetMapping("/due-report/{schoolId}")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyFeesDueReport(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busMonthlyFeeService.getMonthlyFeesDueReport(schoolId, academicYearId));
    }

    @GetMapping("/breakdown/{studentId}")
    public ResponseEntity<List<Map<String, Object>>> getStudentMonthlyFeesBreakdown(
            @PathVariable Long studentId,
            @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busMonthlyFeeService.getStudentMonthlyFeesBreakdown(studentId, academicYearId));
    }
}

