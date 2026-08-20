package com.smartschool.api.serviceImpl;

import com.smartschool.api.dto.AcademicYearChangeRequest;
import com.smartschool.api.dto.AcademicYearStatusResponse;
import com.smartschool.api.dto.StudentExcelDTO;
import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AcademicYearServiceImpl implements AcademicYearService {

    @Autowired private AcademicYearConfigRepository configRepository;
    @Autowired private SchoolRepository schoolRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private FeePaymentRepository feePaymentRepository;
    @Autowired private FeeStructureRepository feeStructureRepository;
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private DailyTimeTableRepository timeTableRepository;
    @Autowired private ClassTeacherRepository classTeacherRepository;
    @Autowired private EmailService emailService;
    @Autowired private StudentFeeDueRepository studentFeeDueRepository;
    @Autowired private StudentService studentService;
    @Autowired private StudentFeeAdjustmentRepository studentFeeAdjustmentRepository;
    @Autowired private BusFeeService busFeeService;
    @Autowired private BusFeeStructureRepository busFeeStructureRepository;
    @Autowired private BusFeePaymentRepository busFeePaymentRepository;
    @Autowired private BusFeeReportService busFeeReportService;

    private final String BACKUP_DIR = "uploads/backups/";

    @Override
    public String getCurrentAcademicYear(Long schoolId) {
        return configRepository
                .findTopBySchoolIdOrderByIdDesc(schoolId)
                .map(AcademicYearConfig::getCurrentYear)
                .orElse("2025-26");
    }

    @Override
    public AcademicYearStatusResponse getStatus(Long schoolId) {
        AcademicYearConfig config = configRepository
                .findTopBySchoolIdOrderByIdDesc(schoolId)
                .orElse(null);
        if (config == null) {
            return new AcademicYearStatusResponse(
                    "2025-26", null, false, 0, "ACTIVE",
                    "Default year chal raha hai.");
        }
        return new AcademicYearStatusResponse(
                config.getCurrentYear(), config.getPreviousYear(),
                config.isEmailSent(), config.getEmailsSentCount(),
                config.getStatus().name(), "Status fetched.");
    }

    @Override
    @Transactional
    public AcademicYearStatusResponse initializeFirstYear(Long schoolId, String currentYear, Long adminUserId) {
        Optional<AcademicYearConfig> existing = configRepository.findTopBySchoolIdOrderByIdDesc(schoolId);
        if (existing.isPresent()) {
            throw new RuntimeException("Is school ke liye Academic Year pehle se set hai!");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found: " + schoolId));

        AcademicYearConfig config = new AcademicYearConfig();
        config.setCurrentYear(currentYear);
        config.setSchool(school);
        config.setChangedByUserId(adminUserId);
        config.setStatus(AcademicYearConfig.YearChangeStatus.ACTIVE);

        AcademicYearConfig savedConfig = configRepository.save(config);

        school.setCurrentYear(savedConfig);
        schoolRepository.save(school);

        log.info("Successfully initialized Year: {} for School: {}", currentYear, school.getSchoolName());

        return new AcademicYearStatusResponse(
                savedConfig.getCurrentYear(), null, false, 0,
                "ACTIVE", "Initial academic year created successfully.");
    }

    @Override
    @Transactional
    public AcademicYearStatusResponse changeAcademicYear(Long schoolId, AcademicYearChangeRequest request) {

        log.warn("Year Change START — School ID: {} | New Year: {}", schoolId, request.getNewYear());

        if (!"CONFIRM".equals(request.getConfirmDelete())) {
            throw new RuntimeException("Safety Check Failed! 'CONFIRM' likhna zaroori hai.");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found: " + schoolId));

        AcademicYearConfig oldConfig = configRepository
                .findTopBySchoolIdOrderByIdDesc(schoolId)
                .orElseThrow(() -> new RuntimeException("No old config found."));

        String oldYearStr = oldConfig.getCurrentYear();
        Long oldYearId = oldConfig.getId();

        AcademicYearConfig newConfig = new AcademicYearConfig();
        newConfig.setCurrentYear(request.getNewYear());
        newConfig.setPreviousYear(oldYearStr);
        newConfig.setSchool(school);
        newConfig.setChangedByUserId(request.getAdminUserId());
        newConfig.setStatus(AcademicYearConfig.YearChangeStatus.CHANGING);
        newConfig = configRepository.save(newConfig);

        try {
            // 1. Generate Excel Data
            byte[] attendanceExcel = buildAttendanceExcel(school.getId(), oldYearId);
            byte[] feesExcel       = buildFeesExcel(school.getId(), oldYearId);
            byte[] expensesExcel   = buildExpensesExcel(school.getId(), oldYearId);
            List<Student> students = studentRepository.findBySchoolIdAndIsActiveTrue(schoolId);
            List<StudentExcelDTO> studentDTOs = students.stream().map(StudentExcelDTO::fromEntity).collect(Collectors.toList());
            byte[] studentsExcel   = studentService.generateStudentReportExcel(studentDTOs);
            byte[] busFeeDuesExcel = busFeeService.generateBusFeeDueReportExcel(schoolId, oldYearId);

            // 2. LAPTOP PE DOWNLOAD KE LIYE SERVER PE SAVE KARO
            saveExcelToLocalFolder(school.getSchoolName(), "Attendance", oldYearStr, attendanceExcel);
            saveExcelToLocalFolder(school.getSchoolName(), "Fees", oldYearStr, feesExcel);
            saveExcelToLocalFolder(school.getSchoolName(), "Expenses", oldYearStr, expensesExcel);
            saveExcelToLocalFolder(school.getSchoolName(), "Students", oldYearStr, studentsExcel);
            saveExcelToLocalFolder(school.getSchoolName(), "Bus_Fee_Dues", oldYearStr, busFeeDuesExcel);

            // 3. EMAIL SENDING
            boolean emailSuccess = false;
            try {
                emailService.sendYearEndDataEmail(
                        school, oldYearStr,
                        attendanceExcel, feesExcel,
                        expensesExcel, studentsExcel, busFeeDuesExcel);
                emailSuccess = true;
            } catch (Exception mailEx) {
                log.error("Email fail: {}", mailEx.getMessage());
            }

            // 3.1 SEND BUS FEES DUE REPORT (NEW FEATURE)
            try {
                busFeeReportService.sendBusFeesReportEmail(school.getId(), oldYearId, school.getMailId());
                log.info("Bus fees due report sent successfully to: {}", school.getMailId());
            } catch (Exception busMailEx) {
                log.warn("Bus fees report email failed: {}", busMailEx.getMessage());
                // Don't fail the entire process if bus fees report fails
            }

            // 4. SAVE DUES & MIGRATE STUDENTS
            savePendingDues(school, oldConfig, newConfig);

            // 5. DELETE & MIGRATE
            deleteAndMigrateData(school.getId(), oldYearId, newConfig);

            school.setCurrentYear(newConfig);
            schoolRepository.save(school);

            newConfig.setStatus(AcademicYearConfig.YearChangeStatus.COMPLETED);
            newConfig.setEmailSent(emailSuccess);
            newConfig.setEmailsSentCount(emailSuccess ? 1 : 0);
            configRepository.save(newConfig);

            return new AcademicYearStatusResponse(
                    newConfig.getCurrentYear(), oldYearStr,
                    emailSuccess, emailSuccess ? 1 : 0, "COMPLETED",
                    "Process complete.");

        } catch (Exception e) {
            log.error("Year Change FAILED: {}", e.getMessage());
            newConfig.setStatus(AcademicYearConfig.YearChangeStatus.ACTIVE);
            configRepository.save(newConfig);
            throw new RuntimeException("Process Failed: " + e.getMessage());
        }
    }

    private void saveExcelToLocalFolder(String schoolName, String type, String year, byte[] data) {
        try {
            Files.createDirectories(Paths.get(BACKUP_DIR));
            String fileName = schoolName.replaceAll(" ", "_") + "_" + type + "_" + year.replaceAll("-", "_") + ".xlsx";
            File file = new File(BACKUP_DIR + fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }
        } catch (Exception e) { log.error("Backup save fail: {}", e.getMessage()); }
    }

    @Transactional
    protected void savePendingDues(School school, AcademicYearConfig oldYear, AcademicYearConfig newYear) {
        List<Student> students = studentRepository.findBySchoolIdAndIsActiveTrue(school.getId());
        for (Student student : students) {
            try {
                Optional<FeeStructure> fsOpt = (student.getSection() != null)
                        ? feeStructureRepository.findBySchoolClassIdAndSectionIdAndAcademicYearIdAndIsActiveTrue(student.getSchoolClass().getId(), student.getSection().getId(), oldYear.getId())
                        : feeStructureRepository.findBySchoolClassIdAndSectionIsNullAndAcademicYearIdAndIsActiveTrue(student.getSchoolClass().getId(), oldYear.getId());

                if (fsOpt.isEmpty()) continue;

                double classFee = fsOpt.get().getTotalFees();

                List<StudentFeeAdjustment> adjustments = studentFeeAdjustmentRepository.findByStudentIdAndAcademicYearIdAndIsArchivedFalse(student.getId(), oldYear.getId());
                double admissionFee = adjustments.stream().filter(a -> a.getFeeType() == StudentFeeAdjustment.FeeAdjustmentType.ADMISSION_FEE).mapToDouble(StudentFeeAdjustment::getAmount).sum();
                double penalty = adjustments.stream().filter(a -> a.getFeeType() == StudentFeeAdjustment.FeeAdjustmentType.PENALTY).mapToDouble(StudentFeeAdjustment::getAmount).sum();
                double extraFee = adjustments.stream().filter(a -> a.getFeeType() == StudentFeeAdjustment.FeeAdjustmentType.EXTRA_FEE).mapToDouble(StudentFeeAdjustment::getAmount).sum();
                double discount = adjustments.stream().filter(a -> a.getFeeType() == StudentFeeAdjustment.FeeAdjustmentType.DISCOUNT).mapToDouble(StudentFeeAdjustment::getAmount).sum();

                double totalBill = classFee + admissionFee + penalty + extraFee - discount;

                List<FeePayment> payments = feePaymentRepository.findByStudentIdAndAcademicYearId(student.getId(), oldYear.getId());
                Double totalPaid = payments.stream().mapToDouble(FeePayment::getAmountPaid).sum();
                Double dueAmount = totalBill - totalPaid;

                if (dueAmount > 1.0) {
                    StudentFeeDue due = new StudentFeeDue();
                    due.setStudent(student);
                    due.setSchool(school);
                    due.setFromAcademicYear(oldYear);
                    due.setToAcademicYear(newYear);
                    due.setFromYear(oldYear.getCurrentYear());
                    due.setToYear(newYear.getCurrentYear());
                    due.setTotalFees(totalBill);
                    due.setTotalPaid(totalPaid);
                    due.setDueAmount(dueAmount);
                    due.setRemainingDue(dueAmount);
                    due.setCleared(false);
                    studentFeeDueRepository.save(due);
                }
            } catch (Exception e) { log.warn("Due skip: {}", e.getMessage()); }
        }
    }

    @Transactional
    protected void deleteAndMigrateData(Long schoolId, Long oldYearId, AcademicYearConfig newYear) {
        attendanceRepository.deleteBySchoolIdAndAcademicYearId(schoolId, oldYearId);
        feePaymentRepository.deleteBySchoolIdAndAcademicYearId(schoolId, oldYearId);
        busFeeStructureRepository.deleteBySchoolIdAndAcademicYearId(schoolId, oldYearId);
        busFeePaymentRepository.deleteBySchoolAndAcademicYear(schoolId, oldYearId);

        List<Student> students = studentRepository.findBySchoolIdAndIsActiveTrue(schoolId);
        for (Student s : students) {
            s.setAcademicYear(newYear);
            studentRepository.save(s);
        }

        timeTableRepository.deactivateAllBySchool(schoolId);
        subjectRepository.deactivateAllBySchool(schoolId);
        classTeacherRepository.deactivateAllBySchool(schoolId);
    }

    // 🚩 FIX: Attendance Excel (Using EnrollmentId for unique identification)
    private byte[] buildAttendanceExcel(Long schoolId, Long yearId) {
        List<Attendance> records = attendanceRepository.findBySchoolIdAndAcademicYearId(schoolId, yearId);
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Attendance");
            Row header = sheet.createRow(0);
            String[] cols = {"Date", "Enrollment ID", "Name", "Class", "Section", "Status"};
            for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);
            int rowNum = 1;
            for (Attendance a : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(a.getAttendanceDate() != null ? a.getAttendanceDate().toString() : "");
                // 🚩 Yahan String-based Enrollment ID use kar rahe hain
                row.createCell(1).setCellValue(a.getStudent() != null ? safe(a.getStudent().getEnrollmentId()) : "");
                row.createCell(2).setCellValue(a.getStudent() != null ? safe(a.getStudent().getName()) : "");
                row.createCell(3).setCellValue(a.getSchoolClass() != null ? safe(a.getSchoolClass().getClassName()) : "");
                row.createCell(4).setCellValue(a.getSection() != null ? safe(a.getSection().getSectionName()) : "N/A");
                row.createCell(5).setCellValue(safe(a.getStatus()));
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(); wb.write(out); return out.toByteArray();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    // 🚩 FIX: Fees Excel
    private byte[] buildFeesExcel(Long schoolId, Long yearId) {
        List<FeePayment> payments = feePaymentRepository.findBySchoolIdAndAcademicYearId(schoolId, yearId);
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Fees");
            Row header = sheet.createRow(0);
            String[] cols = {"Receipt", "Date", "Enrollment ID", "Amount Paid", "Remaining"};
            for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);
            int rowNum = 1;
            for (FeePayment f : payments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(safe(f.getReceiptNumber()));
                row.createCell(1).setCellValue(f.getPaymentDate() != null ? f.getPaymentDate().toString() : "");
                // 🚩 String check
                row.createCell(2).setCellValue(f.getStudent() != null ? safe(f.getStudent().getEnrollmentId()) : "");
                row.createCell(3).setCellValue(f.getAmountPaid() != null ? f.getAmountPaid() : 0.0);
                row.createCell(4).setCellValue(f.getRemainingBalance() != null ? f.getRemainingBalance() : 0.0);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(); wb.write(out); return out.toByteArray();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    private byte[] buildExpensesExcel(Long schoolId, Long yearId) {
        List<Expense> expenses = expenseRepository.findBySchoolIdAndAcademicYearId(schoolId, yearId);
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Expenses");
            Row header = sheet.createRow(0);
            String[] cols = {"Expense Name", "Category", "Amount", "Date"};
            for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);
            int rowNum = 1;
            for (Expense e : expenses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(safe(e.getExpenseName()));
                row.createCell(1).setCellValue(safe(e.getCategory()));
                row.createCell(2).setCellValue(e.getAmount() != null ? e.getAmount() : 0.0);
                row.createCell(3).setCellValue(e.getDate() != null ? e.getDate().toString() : "");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(); wb.write(out); return out.toByteArray();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    private String safe(String v) { return (v == null) ? "" : v; }
}