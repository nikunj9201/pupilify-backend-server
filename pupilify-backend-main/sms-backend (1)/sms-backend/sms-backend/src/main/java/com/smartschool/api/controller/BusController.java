package com.smartschool.api.controller;

import com.smartschool.api.entity.BusFeePayment;
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

    @PostMapping("/assign-student")
    public ResponseEntity<StudentBusAssignment> assignStudentToBus(@RequestParam Long studentId, @RequestParam Long stoppageId, @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busService.assignStudentToBus(studentId, stoppageId, academicYearId));
    }

    @PostMapping("/collect-fee")
    public ResponseEntity<BusFeePayment> collectBusFee(@RequestParam Long studentId, @RequestParam Long academicYearId, @RequestParam double amount, @RequestParam String paymentMode) {
        return ResponseEntity.ok(busService.collectBusFee(studentId, academicYearId, amount, paymentMode));
    }

    @GetMapping("/due-report/{schoolId}")
    public ResponseEntity<List<Map<String, Object>>> getBusFeeDueReport(@PathVariable Long schoolId, @RequestParam Long academicYearId) {
        return ResponseEntity.ok(busService.getBusFeeDueReport(schoolId, academicYearId));
    }
}