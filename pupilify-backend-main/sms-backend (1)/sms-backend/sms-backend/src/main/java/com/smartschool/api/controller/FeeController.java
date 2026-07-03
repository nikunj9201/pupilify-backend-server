package com.smartschool.api.controller;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin/fees")
@CrossOrigin("*")
public class FeeController {

    @Autowired private FeeStructureRepository feeStructureRepository;
    @Autowired private FeePaymentRepository feePaymentRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private SchoolRepository schoolRepository;
    @Autowired private ClassRepository classRepository;
    @Autowired private SectionRepository sectionRepository;
    @Autowired private AcademicYearConfigRepository yearRepo;
    @Autowired private StudentFeeDueRepository studentFeeDueRepository;
    @Autowired private StudentFeeAdjustmentRepository studentFeeAdjustmentRepository;

    // ─────────────────────────────────────────────────────────
    // 1. FEE STRUCTURE APIs
    // ─────────────────────────────────────────────────────────

    @PostMapping("/structure/set/{schoolId}")
    public ResponseEntity<FeeStructure> setFeeStructure(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestBody FeeStructure structure) {

        School school = schoolRepository.findById(schoolId).orElseThrow();
        AcademicYearConfig year = yearRepo.findById(academicYearId)
                .orElseThrow(() -> new RuntimeException("Academic Year not found"));

        structure.setSchool(school);
        structure.setAcademicYear(year);
        structure.setActive(true);
        return ResponseEntity.ok(feeStructureRepository.save(structure));
    }

    @GetMapping("/structure/school/{schoolId}")
    public ResponseEntity<List<FeeStructure>> getFeeStructures(@PathVariable Long schoolId) {
        return ResponseEntity.ok(feeStructureRepository.findBySchoolIdAndIsActiveTrue(schoolId));
    }

    // ─────────────────────────────────────────────────────────
    // 2. FEE COLLECTION APIs (Fixed for Across-Year Dues)
    // ─────────────────────────────────────────────────────────

    @GetMapping("/fetch-details/{enrollmentId}/{schoolId}")
    public ResponseEntity<?> getStudentFeeDetails(
            @PathVariable String enrollmentId,
            @PathVariable Long schoolId) {

        Student student = studentRepository.findByEnrollmentIdAndIsActiveTrue(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + enrollmentId));

        if (!student.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Student is not part of this school!");
        }

        Long yearId = student.getAcademicYear() != null ? student.getAcademicYear().getId() : null;

        if (yearId == null) {
            throw new RuntimeException("Student ka Academic Year set nahi hai!");
        }

        FeeStructure fs = feeStructureRepository
                .findBySchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(
                        student.getSchoolClass().getId(),
                        student.getSection() != null ? student.getSection().getId() : null,
                        yearId)
                .orElseGet(() -> feeStructureRepository
                        .findBySchoolClassIdAndSectionIsNullAndAcademicYearIdAndIsActiveTrue(
                                student.getSchoolClass().getId(), yearId)
                        .orElseThrow(() -> new RuntimeException("Fee Structure not defined for this class")));

        List<FeePayment> history = feePaymentRepository.findByStudentIdAndAcademicYearId(student.getId(), yearId);

        Double totalPaidThisYear = history.stream()
                .filter(p -> p.getReceiptNumber() != null && !p.getReceiptNumber().startsWith("DUE-"))
                .mapToDouble(FeePayment::getAmountPaid).sum();

        // 🚩 FEATURE ADDED: Fetching dues by Enrollment ID to persist across Academic Years
        List<StudentFeeDue> pendingDues = studentFeeDueRepository.findByEnrollmentIdAndClearedFalse(student.getEnrollmentId());
        Double totalOldDueRemaining = pendingDues.stream().mapToDouble(StudentFeeDue::getRemainingDue).sum();

        Double currentYearTotal = fs.getTotalFees();
        Double currentYearRemaining = currentYearTotal - totalPaidThisYear;

        Map<String, Object> details = new HashMap<>();
        details.put("studentId", student.getId());
        details.put("enrollmentId", student.getEnrollmentId());
        details.put("rollNumber", student.getRollNumber());
        details.put("studentName", student.getName());
        details.put("className", student.getSchoolClass().getClassName());
        details.put("sectionName", student.getSection() != null ? student.getSection().getSectionName() : "N/A");

        details.put("totalClassFees", currentYearTotal);
        details.put("totalPaidThisYear", totalPaidThisYear);
        details.put("currentYearDue", currentYearRemaining);
        details.put("previousYearDue", totalOldDueRemaining);
        details.put("grandTotalDue", currentYearRemaining + totalOldDueRemaining);

        details.put("academicYear", fs.getAcademicYear() != null ? fs.getAcademicYear().getCurrentYear() : "N/A");
        details.put("previousYearDues", pendingDues);

        return ResponseEntity.ok(details);
    }

    @PostMapping("/collect/{schoolId}")
    @Transactional
    public ResponseEntity<?> collectFees(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestBody FeePayment payment) {
        try {
            Student tempStudent = null;
            if (payment.getStudent() != null && payment.getStudent().getId() != null) {
                tempStudent = studentRepository.findById(payment.getStudent().getId()).orElse(null);
            } else if (payment.getStudent() != null && payment.getStudent().getEnrollmentId() != null) {
                tempStudent = studentRepository.findByEnrollmentIdAndIsActiveTrue(payment.getStudent().getEnrollmentId()).orElse(null);
            }

            if (tempStudent == null) throw new RuntimeException("Student record nahi mila!");

            final Student student = tempStudent;

            School school = schoolRepository.findById(schoolId).orElseThrow();
            AcademicYearConfig year = yearRepo.findById(academicYearId).orElseThrow();

            FeeStructure fs = feeStructureRepository
                    .findBySchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(
                            student.getSchoolClass().getId(),
                            student.getSection() != null ? student.getSection().getId() : null,
                            academicYearId)
                    .orElseGet(() -> feeStructureRepository
                            .findBySchoolClassIdAndSectionIsNullAndAcademicYearIdAndIsActiveTrue(
                                    student.getSchoolClass().getId(), academicYearId)
                            .orElseThrow(() -> new RuntimeException("Fee Structure missing!")));

            List<FeePayment> history = feePaymentRepository.findByStudentIdAndAcademicYearId(student.getId(), academicYearId);
            Double alreadyPaid = history.stream()
                    .filter(p -> p.getReceiptNumber() != null && !p.getReceiptNumber().startsWith("DUE-"))
                    .mapToDouble(FeePayment::getAmountPaid).sum();

            Double totalFees = fs.getTotalFees();
            Double newRemaining = totalFees - (alreadyPaid + payment.getAmountPaid());

            payment.setStudent(student);
            payment.setSchool(school);
            payment.setSchoolClass(student.getSchoolClass());
            payment.setAcademicYear(year);
            payment.setRollNumber(student.getEnrollmentId());

            payment.setTotalClassFees(totalFees);
            payment.setRemainingBalance(newRemaining);

            if (payment.getSection() != null && payment.getSection().getId() != null) {
                Section section = sectionRepository.findById(payment.getSection().getId()).orElse(student.getSection());
                payment.setSection(section);
            } else {
                payment.setSection(student.getSection());
            }

            payment.setPaymentDate(LocalDate.now());
            payment.setReceiptNumber("RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            return ResponseEntity.ok(feePaymentRepository.save(payment));
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    // 3. FILTERING & DUE REPORT APIs
    // ─────────────────────────────────────────────────────────

    @GetMapping("/due-report/{schoolId}")
    public ResponseEntity<List<Map<String, Object>>> getSchoolDueReport(@PathVariable Long schoolId, @RequestParam Long academicYearId) {
        List<Student> students = studentRepository.findBySchoolIdAndIsActiveTrue(schoolId);
        return ResponseEntity.ok(generateDueReport(students, academicYearId));
    }

    @GetMapping("/due-report/{schoolId}/{classId}")
    public ResponseEntity<List<Map<String, Object>>> getClassDueReport(@PathVariable Long schoolId, @PathVariable Long classId, @RequestParam Long academicYearId) {
        List<Student> students = studentRepository.findBySchoolIdAndSchoolClassIdAndAcademicYearIdAndIsActiveTrue(schoolId, classId, academicYearId);
        return ResponseEntity.ok(generateDueReport(students, academicYearId));
    }

    @GetMapping("/due-report/{schoolId}/{classId}/{sectionId}")
    public ResponseEntity<List<Map<String, Object>>> getSectionDueReport(@PathVariable Long schoolId, @PathVariable Long classId, @PathVariable Long sectionId, @RequestParam Long academicYearId) {
        List<Student> students = studentRepository.findBySchoolIdAndSchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(schoolId, classId, sectionId, academicYearId);
        return ResponseEntity.ok(generateDueReport(students, academicYearId));
    }

    private List<Map<String, Object>> generateDueReport(List<Student> students, Long yearId) {
        List<Map<String, Object>> report = new ArrayList<>();
        for (Student student : students) {
            try {
                Optional<FeeStructure> fsOpt = (student.getSection() != null)
                        ? feeStructureRepository.findBySchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(student.getSchoolClass().getId(), student.getSection().getId(), yearId)
                        : feeStructureRepository.findBySchoolClassIdAndSectionIsNullAndAcademicYearIdAndIsActiveTrue(student.getSchoolClass().getId(), yearId);

                if (fsOpt.isPresent()) {
                    Double currentYearTotalFees = fsOpt.get().getTotalFees();

                    List<FeePayment> history = feePaymentRepository.findByStudentIdAndAcademicYearId(student.getId(), yearId);
                    Double paidThisYear = history.stream()
                            .filter(p -> p.getReceiptNumber() != null && !p.getReceiptNumber().startsWith("DUE-"))
                            .mapToDouble(FeePayment::getAmountPaid).sum();

                    // 🚩 FEATURE ADDED: Fetching dues by Enrollment ID in report
                    List<StudentFeeDue> oldDues = studentFeeDueRepository.findByEnrollmentIdAndClearedFalse(student.getEnrollmentId());
                    Double remainingOldDue = oldDues.stream().mapToDouble(StudentFeeDue::getRemainingDue).sum();

                    // 🚩 FIX: Get student name with proper null checks and fallback
                    String studentName = student.getName();
                    if (studentName == null || studentName.trim().isEmpty()) {
                        // Fallback to User entity username (email) if Student name is empty
                        if (student.getUser() != null && student.getUser().getUsername() != null) {
                            studentName = student.getUser().getUsername();
                        } else {
                            studentName = "N/A";  // Final fallback
                        }
                    }

                    Map<String, Object> row = new HashMap<>();
                    row.put("enrollmentId", student.getEnrollmentId());
                    row.put("rollNumber", student.getRollNumber());
                    row.put("studentName", studentName);  // 🚩 Using fixed value
                    row.put("className", student.getSchoolClass().getClassName());
                    row.put("sectionName", student.getSection() != null ? student.getSection().getSectionName() : "N/A");
                    row.put("currentYearFees", currentYearTotalFees);
                    row.put("paidThisYear", paidThisYear);
                    row.put("previousYearDue", remainingOldDue);
                    row.put("grandTotalDue", (currentYearTotalFees - paidThisYear) + remainingOldDue);
                    row.put("hasPreviousYearDue", remainingOldDue > 0);

                    report.add(row);
                }
            } catch (Exception e) { /* Skip */ }
        }
        return report;
    }

    // ─────────────────────────────────────────────────────────
    // 4. SOFT DELETE
    // ─────────────────────────────────────────────────────────

    @DeleteMapping("/structure/delete/{id}")
    public ResponseEntity<String> archiveStructure(@PathVariable Long id) {
        FeeStructure fs = feeStructureRepository.findById(id).orElseThrow();
        fs.setActive(false);
        feeStructureRepository.save(fs);
        return ResponseEntity.ok("Fee Structure archived. Records preserved.");
    }

    // ═════════════════════════════════════════════════════════
    // 5. PREVIOUS YEAR DUE FEES APIs
    // ═════════════════════════════════════════════════════════

    @GetMapping("/dues/student/{studentId}")
    public ResponseEntity<?> getStudentPreviousDues(@PathVariable Long studentId) {
        try {
            // Numeric studentId se pehle student dhoondo
            Student s = studentRepository.findById(studentId).orElseThrow();
            // 🚩 Enrollment ID se dues dhoondo taaki pichle saal ka data dikhe
            List<StudentFeeDue> dues = studentFeeDueRepository.findByEnrollmentIdAndClearedFalse(s.getEnrollmentId());

            Double totalPending = dues.stream().mapToDouble(StudentFeeDue::getRemainingDue).sum();
            Map<String, Object> response = new HashMap<>();
            response.put("studentId", studentId);
            response.put("pendingDues", dues);
            response.put("totalPendingAmount", totalPending);
            response.put("totalDueCount", dues.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/dues/pay/{dueId}/{schoolId}")
    @Transactional
    public ResponseEntity<?> payPreviousYearDue(@PathVariable Long dueId, @PathVariable Long schoolId, @RequestParam Double amountPaying, @RequestParam Long academicYearId, @RequestParam String paymentMode) {
        try {
            StudentFeeDue due = studentFeeDueRepository.findById(dueId).orElseThrow(() -> new RuntimeException("Due record nahi mila"));
            if (due.isCleared()) return ResponseEntity.badRequest().body("Yeh due pehle se clear ho chuki hai!");
            if (amountPaying > due.getRemainingDue()) return ResponseEntity.badRequest().body("Amount zyada hai!");

            due.setPaidFromDue(due.getPaidFromDue() + amountPaying);
            due.setRemainingDue(due.getRemainingDue() - amountPaying);
            if (due.getRemainingDue() <= 0.5) { due.setCleared(true); due.setRemainingDue(0.0); }
            studentFeeDueRepository.save(due);

            AcademicYearConfig currentYear = yearRepo.findById(academicYearId).orElseThrow();
            FeePayment payment = new FeePayment();
            payment.setStudent(due.getStudent());
            payment.setSchool(schoolRepository.findById(schoolId).orElseThrow());
            payment.setSchoolClass(due.getStudent().getSchoolClass());
            payment.setAcademicYear(currentYear);
            payment.setAmountPaid(amountPaying);
            payment.setPaymentDate(LocalDate.now());
            payment.setPaymentMode(paymentMode);
            payment.setRollNumber(due.getStudent().getEnrollmentId());
            payment.setReceiptNumber("DUE-RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            payment.setTotalClassFees(0.0);
            payment.setRemainingBalance(0.0);

            feePaymentRepository.save(payment);

            return ResponseEntity.ok(Map.of("success", true, "message", "Due payment successful"));
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    @GetMapping("/dues/history/{studentId}")
    public ResponseEntity<?> getStudentDueHistory(@PathVariable Long studentId) {
        try {
            Student s = studentRepository.findById(studentId).orElseThrow();
            // 🚩 Enrollment ID se history dhoondo
            List<StudentFeeDue> allDues = studentFeeDueRepository.findByEnrollmentId(s.getEnrollmentId());

            Double totalDueEver = allDues.stream().mapToDouble(StudentFeeDue::getDueAmount).sum();
            Double totalClearedAmount = allDues.stream().mapToDouble(StudentFeeDue::getPaidFromDue).sum();
            Double stillPending = allDues.stream().filter(d -> !d.isCleared()).mapToDouble(StudentFeeDue::getRemainingDue).sum();
            Map<String, Object> response = new HashMap<>();
            response.put("allDues", allDues);
            response.put("totalDueEver", totalDueEver);
            response.put("totalClearedAmount", totalClearedAmount);
            response.put("stillPending", stillPending);
            return ResponseEntity.ok(response);
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    // ─────────────────────────────────────────────────────────
    // 6. STUDENT FEE ADJUSTMENTS
    // ─────────────────────────────────────────────────────────

    @PostMapping("/adjustments/add/{enrollmentId}")
    public ResponseEntity<StudentFeeAdjustment> addFeeAdjustment(
            @PathVariable String enrollmentId,
            @RequestBody StudentFeeAdjustment adjustment) {
        Student student = studentRepository.findByEnrollmentIdAndIsActiveTrue(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + enrollmentId));
        adjustment.setStudent(student);
        return ResponseEntity.ok(studentFeeAdjustmentRepository.save(adjustment));
    }

    @GetMapping("/record/{enrollmentId}")
    public ResponseEntity<?> getFeeRecord(@PathVariable String enrollmentId) {
        Student student = studentRepository.findByEnrollmentIdAndIsActiveTrue(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + enrollmentId));

        Long yearId = student.getAcademicYear() != null ? student.getAcademicYear().getId() : null;
        if (yearId == null) {
            throw new RuntimeException("Student's Academic Year is not set!");
        }

        FeeStructure fs = feeStructureRepository
                .findBySchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(
                        student.getSchoolClass().getId(),
                        student.getSection() != null ? student.getSection().getId() : null,
                        yearId)
                .orElseGet(() -> feeStructureRepository
                        .findBySchoolClassIdAndSectionIsNullAndAcademicYearIdAndIsActiveTrue(
                                student.getSchoolClass().getId(), yearId)
                        .orElseThrow(() -> new RuntimeException("Fee Structure not defined for this class")));

        List<StudentFeeAdjustment> adjustments = studentFeeAdjustmentRepository.findByStudentId(student.getId());
        double admissionFee = adjustments.stream().filter(a -> a.getFeeType() == StudentFeeAdjustment.FeeAdjustmentType.ADMISSION_FEE).mapToDouble(StudentFeeAdjustment::getAmount).sum();
        double penalty = adjustments.stream().filter(a -> a.getFeeType() == StudentFeeAdjustment.FeeAdjustmentType.PENALTY).mapToDouble(StudentFeeAdjustment::getAmount).sum();
        double extraFee = adjustments.stream().filter(a -> a.getFeeType() == StudentFeeAdjustment.FeeAdjustmentType.EXTRA_FEE).mapToDouble(StudentFeeAdjustment::getAmount).sum();
        double discount = adjustments.stream().filter(a -> a.getFeeType() == StudentFeeAdjustment.FeeAdjustmentType.DISCOUNT).mapToDouble(StudentFeeAdjustment::getAmount).sum();

        double totalFees = fs.getTotalFees() + admissionFee + penalty + extraFee - discount;

        List<FeePayment> history = feePaymentRepository.findByStudentIdAndAcademicYearId(student.getId(), yearId);
        Double totalPaidThisYear = history.stream()
                .filter(p -> p.getReceiptNumber() != null && !p.getReceiptNumber().startsWith("DUE-"))
                .mapToDouble(FeePayment::getAmountPaid).sum();

        List<StudentFeeDue> pendingDues = studentFeeDueRepository.findByEnrollmentIdAndClearedFalse(student.getEnrollmentId());
        Double totalOldDueRemaining = pendingDues.stream().mapToDouble(StudentFeeDue::getRemainingDue).sum();

        Map<String, Object> details = new HashMap<>();
        details.put("studentName", student.getName());
        details.put("className", student.getSchoolClass().getClassName());
        details.put("totalClassFees", fs.getTotalFees());
        details.put("admissionFee", admissionFee);
        details.put("penalty", penalty);
        details.put("extraFee", extraFee);
        details.put("discount", discount);
        details.put("totalFees", totalFees);
        details.put("totalPaidThisYear", totalPaidThisYear);
        details.put("remainingBalance", totalFees - totalPaidThisYear);
        details.put("previousYearDue", totalOldDueRemaining);
        details.put("grandTotalDue", (totalFees - totalPaidThisYear) + totalOldDueRemaining);
        details.put("feeAdjustments", adjustments);
        details.put("paymentHistory", history);

        return ResponseEntity.ok(details);
    }
}