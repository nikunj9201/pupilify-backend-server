package com.smartschool.api.controller;

import com.smartschool.api.entity.BusFeePayment;
import com.smartschool.api.entity.BusFeeStructure;
import com.smartschool.api.service.BusFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bus-fees")
@CrossOrigin("*")
public class BusFeeController {

    @Autowired
    private BusFeeService busFeeService;

    @PostMapping("/admin/structure/bulk")
    public ResponseEntity<List<BusFeeStructure>> createBulkFeeStructure(@RequestParam Long schoolId,
                                                                         @RequestParam Long busId,
                                                                         @RequestParam Long academicYearId,
                                                                         @RequestBody List<BusFeeStructure> feeStructures) {
        return ResponseEntity.ok(busFeeService.createBulkFeeStructure(schoolId, busId, academicYearId, feeStructures));
    }

    @PutMapping("/admin/structure/{structureId}")
    public ResponseEntity<BusFeeStructure> updateFeeStructure(@PathVariable Long structureId, @RequestParam double amount) {
        return ResponseEntity.ok(busFeeService.updateFeeStructure(structureId, amount));
    }

    @DeleteMapping("/admin/structure/{structureId}")
    public ResponseEntity<Void> deleteFeeStructure(@PathVariable Long structureId) {
        busFeeService.deleteFeeStructure(structureId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/collect-fee")
    public ResponseEntity<BusFeePayment> collectFee(@RequestParam String studentIdentifier, @RequestParam double amount, @RequestParam String paymentMode) {
        return ResponseEntity.ok(busFeeService.collectFee(studentIdentifier, amount, paymentMode));
    }

    @GetMapping("/admin/due-report/{busId}")
    public ResponseEntity<List<Map<String, Object>>> getDueReport(@PathVariable Long busId) {
        return ResponseEntity.ok(busFeeService.getDueReport(busId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<BusFeeStructure>> getStudentFeeStructure(@PathVariable Long studentId) {
        return ResponseEntity.ok(busFeeService.getStudentFeeStructure(studentId));
    }
}