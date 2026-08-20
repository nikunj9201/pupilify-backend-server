package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.BusFeeReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BusFeeReportServiceImpl implements BusFeeReportService {

    @Autowired
    private StudentMonthlyFeeStructureRepository studentFeeRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private AcademicYearConfigRepository academicYearRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public List<StudentMonthlyFeeStructure> getStudentsWithDues(Long schoolId, Long academicYearId) {
        // Get all active student fee assignments for the school
        return studentFeeRepository.findBySchoolIdAndAcademicYearIdAndActiveTrue(schoolId, academicYearId);
    }

    @Override
    public ByteArrayOutputStream generateBusFeesDueExcel(Long schoolId, Long academicYearId) throws IOException {
        List<StudentMonthlyFeeStructure> dues = getStudentsWithDues(schoolId, academicYearId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Fees Due");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);

        // Create currency style
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("₹#,##0.00"));
        currencyStyle.setBorderBottom(BorderStyle.THIN);
        currencyStyle.setBorderTop(BorderStyle.THIN);
        currencyStyle.setBorderRight(BorderStyle.THIN);
        currencyStyle.setBorderLeft(BorderStyle.THIN);

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Student ID", "Student Name", "Enrollment ID", "Class", "Section", "Stoppage",
                            "Monthly Fee", "No. of Months", "Total Due Amount", "Payment Frequency", "Joining Month"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Set column widths
        sheet.setColumnWidth(0, 5000);  // Student ID
        sheet.setColumnWidth(1, 6000);  // Student Name
        sheet.setColumnWidth(2, 5000);  // Enrollment ID
        sheet.setColumnWidth(3, 4000);  // Class
        sheet.setColumnWidth(4, 4000);  // Section
        sheet.setColumnWidth(5, 6000);  // Stoppage
        sheet.setColumnWidth(6, 5000);  // Monthly Fee
        sheet.setColumnWidth(7, 5000);  // Months
        sheet.setColumnWidth(8, 6000);  // Total Due
        sheet.setColumnWidth(9, 5000);  // Payment Freq
        sheet.setColumnWidth(10, 5000); // Joining Month

        // Fill data rows
        int rowNum = 1;
        double totalDue = 0;

        for (StudentMonthlyFeeStructure fee : dues) {
            Row row = sheet.createRow(rowNum);

            row.createCell(0).setCellValue(fee.getStudent().getId());
            row.createCell(1).setCellValue(fee.getStudent().getName());
            row.createCell(2).setCellValue(fee.getStudent().getEnrollmentId());
            row.createCell(3).setCellValue(fee.getStudent().getSchoolClass() != null ?
                    fee.getStudent().getSchoolClass().getClassName() : "N/A");
            row.createCell(4).setCellValue(fee.getStudent().getSection() != null ?
                    fee.getStudent().getSection().getSectionName() : "N/A");
            row.createCell(5).setCellValue(fee.getStoppage().getStopName());

            Cell feeCell = row.createCell(6);
            feeCell.setCellValue(fee.getMonthlyFeeAmount());
            feeCell.setCellStyle(currencyStyle);

            // Count months from JSON
            int monthCount = countMonths(fee.getSelectedMonths());
            row.createCell(7).setCellValue(monthCount);

            Cell totalCell = row.createCell(8);
            totalCell.setCellValue(fee.getTotalFeeAmount());
            totalCell.setCellStyle(currencyStyle);

            row.createCell(9).setCellValue(fee.getPaymentFrequency().toString());
            row.createCell(10).setCellValue(fee.getJoiningMonth());

            totalDue += fee.getTotalFeeAmount();
            rowNum++;
        }

        // Add summary row
        if (rowNum > 1) {
            Row summaryRow = sheet.createRow(rowNum + 1);
            Cell summaryLabel = summaryRow.createCell(7);
            summaryLabel.setCellValue("TOTAL DUE:");
            summaryLabel.setCellStyle(headerStyle);

            Cell summaryValue = summaryRow.createCell(8);
            summaryValue.setCellValue(totalDue);
            summaryValue.setCellStyle(currencyStyle);
        }

        // Write to output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }

    @Override
    public void sendBusFeesReportEmail(Long schoolId, Long academicYearId, String toEmail) throws IOException {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));
        AcademicYearConfig academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new RuntimeException("Academic year not found"));

        List<StudentMonthlyFeeStructure> dues = getStudentsWithDues(schoolId, academicYearId);

        if (dues.isEmpty()) {
            // No dues, no need to send email
            return;
        }

        ByteArrayOutputStream excelFile = generateBusFeesDueExcel(schoolId, academicYearId);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setFrom("noreply@smartschool.com");
            helper.setSubject("Bus Fees Due Report - " + school.getSchoolName() + " - Academic Year " + academicYear.getCurrentYear());

            String emailContent = "Dear Administrator,\n\n" +
                    "Please find attached the Bus Fees Due Report for academic year " + academicYear.getCurrentYear() + ".\n\n" +
                    "Total Students with Dues: " + dues.size() + "\n" +
                    "Generated On: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()) + "\n\n" +
                    "This is an automated report. Please do not reply to this email.\n\n" +
                    "Regards,\nSmart School System";

            helper.setText(emailContent);
            helper.addAttachment("BusFeesReport_" + System.currentTimeMillis() + ".xlsx", () ->
                    new java.io.ByteArrayInputStream(excelFile.toByteArray()), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private int countMonths(String selectedMonthsJson) {
        if (selectedMonthsJson == null || selectedMonthsJson.isEmpty()) {
            return 0;
        }
        // Simple count of commas + 1 to get month count
        // Format is ["July", "August", ...] so count commas + 1
        return (int) selectedMonthsJson.chars().filter(ch -> ch == ',').count() + 1;
    }
}

