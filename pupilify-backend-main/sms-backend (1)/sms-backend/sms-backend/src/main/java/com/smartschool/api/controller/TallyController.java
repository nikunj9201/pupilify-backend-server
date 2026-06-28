package com.smartschool.api.controller;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tally")
@CrossOrigin("*")
public class TallyController {

    @Autowired private ExpenseRepository expenseRepo;
    @Autowired private FeePaymentRepository feeRepo;
    @Autowired private AcademicYearConfigRepository yearRepo;
    @Autowired private SchoolRepository schoolRepo; // 🚩 School fetch karne ke liye repository add ki

    // 🚩 1. Expense Add Karna (Updated with School Mapping)
    @PostMapping("/add-expense/{schoolId}")
    public ResponseEntity<Expense> addExpense(
            @PathVariable Long schoolId,
            @RequestBody Expense expense,
            @RequestParam Long yearId) {

        // Academic Year set karein
        AcademicYearConfig year = yearRepo.findById(yearId)
                .orElseThrow(() -> new RuntimeException("Academic Year not found"));
        expense.setAcademicYear(year);

        // ✅ FIXED: School mapping add ki taki database mein school_id NULL na jaye
        School school = schoolRepo.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));
        expense.setSchool(school);

        return ResponseEntity.ok(expenseRepo.save(expense));
    }

    // 🚩 2. Expense Delete Karna
    @DeleteMapping("/delete-expense/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        expenseRepo.deleteById(id);
        return ResponseEntity.ok("Expense deleted successfully!");
    }

    // 🚩 3. Tally Dashboard Data (Profit/Loss Logic)
    @GetMapping("/summary/{schoolId}")
    public ResponseEntity<Map<String, Object>> getTallySummary(
            @PathVariable Long schoolId,
            @RequestParam Long yearId) {

        AcademicYearConfig year = yearRepo.findById(yearId)
                .orElseThrow(() -> new RuntimeException("Academic Year not found"));

        // Fees fetch karein (Credit)
        Double totalFees = feeRepo.getTotalFeesCollectedBySchool(schoolId, year.getId());
        if (totalFees == null) totalFees = 0.0;

        // Total Expenses fetch karein (Debit)
        Double totalExpenses = expenseRepo.getTotalExpensesBySchool(schoolId, yearId);
        if (totalExpenses == null) totalExpenses = 0.0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalFeesCollected", totalFees);
        summary.put("totalExpenses", totalExpenses);
        summary.put("netProfit", totalFees - totalExpenses); // Profit calculation
        summary.put("year", year.getCurrentYear());

        return ResponseEntity.ok(summary);
    }

    // 🚩 4. Expenses List dekhna
    @GetMapping("/expenses/{schoolId}")
    public ResponseEntity<List<Expense>> listExpenses(
            @PathVariable Long schoolId,
            @RequestParam Long yearId) {
        return ResponseEntity.ok(expenseRepo.findBySchoolIdAndAcademicYearId(schoolId, yearId));
    }
}