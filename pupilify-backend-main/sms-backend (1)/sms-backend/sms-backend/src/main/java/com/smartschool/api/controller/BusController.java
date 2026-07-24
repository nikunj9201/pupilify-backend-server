package com.smartschool.api.controller;

import com.smartschool.api.dto.BusAssignmentDTO;
import com.smartschool.api.dto.TransportFeeLogDTO;
import com.smartschool.api.entity.Bus;
import com.smartschool.api.entity.StudentBusAssignment;
import com.smartschool.api.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/buses")
@CrossOrigin("*")
public class BusController {

    @Autowired
    private BusService busService;

    @PostMapping("/admin/{schoolId}/add")
    public ResponseEntity<Bus> addBus(@PathVariable Long schoolId, @RequestParam String registrationNo, @RequestParam int capacity) {
        return ResponseEntity.ok(busService.addBus(schoolId, registrationNo, capacity));
    }

    @GetMapping("/admin/school/{schoolId}")
    public ResponseEntity<List<Bus>> getBusesBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(busService.getBusesBySchool(schoolId));
    }

    @DeleteMapping("/admin/delete/{schoolId}/{busId}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long schoolId, @PathVariable Long busId) {
        busService.deleteBus(schoolId, busId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign-student")
    public ResponseEntity<StudentBusAssignment> assignStudentToBus(@RequestParam Long studentId, @RequestParam Long stoppageId, @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busService.assignStudentToBus(studentId, stoppageId, academicYearId));
    }

    @PostMapping("/admin/generate-monthly-fees")
    public ResponseEntity<Void> generateMonthlyFees(@RequestParam Long schoolId, @RequestParam Long academicYearId, @RequestBody List<String> months) {
        busService.generateMonthlyFees(schoolId, academicYearId, months);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/collect-fee")
    public ResponseEntity<TransportFeeLogDTO> collectBusFee(@RequestParam Long studentId, @RequestParam Long academicYearId, @RequestParam double amount, @RequestParam String paymentMode) {
        return ResponseEntity.ok(busService.collectBusFee(studentId, academicYearId, amount, paymentMode));
    }

    @GetMapping("/admin/due-report/{schoolId}")
    public ResponseEntity<List<Map<String, Object>>> getBusFeeDueReport(@PathVariable Long schoolId, @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busService.getBusFeeDueReport(schoolId, academicYearId));
    }

    @GetMapping("/student-assignment/{studentId}")
    public ResponseEntity<StudentBusAssignment> getStudentBusAssignment(@PathVariable Long studentId, @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busService.getStudentBusAssignment(studentId, academicYearId));
    }

    @GetMapping("/assignments/route/{routeId}")
    public ResponseEntity<List<BusAssignmentDTO>> getAssignmentsByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(busService.getAssignmentsByRoute(routeId));
    }

    @GetMapping("/fee-history/{studentId}")
    public ResponseEntity<List<TransportFeeLogDTO>> getFeeHistory(@PathVariable Long studentId, @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busService.getFeeHistory(studentId, academicYearId));
    }
}