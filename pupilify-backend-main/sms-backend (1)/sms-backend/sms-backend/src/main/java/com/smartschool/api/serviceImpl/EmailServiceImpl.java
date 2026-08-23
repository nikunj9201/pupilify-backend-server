package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.School;
import com.smartschool.api.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendYearEndDataEmail(
            School school, String year,
            byte[] attendanceExcel,
            byte[] feesExcel,
            byte[] expensesExcel,
            byte[] studentsExcel,
            byte[] busFeeDuesExcel) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(school.getMailId());
            helper.setSubject("[SmartSchool] Academic Year " + year
                    + " — Year-End Data Report");

            String body = """
                    <html><body>
                    <h2>Academic Year %s — Year-End Report</h2>
                    <p>Namaste <b>%s</b>,</p>
                    <p>Academic year change se pehle aapke school ka poora data
                    neeche 5 Excel files mein attach kiya gaya hai:</p>
                    <ul>
                        <li><b>Attendance_Report.xlsx</b> — Poori saal ki attendance</li>
                        <li><b>Fees_Report.xlsx</b> — Saari fee payments</li>
                        <li><b>Expenses_Report.xlsx</b> — School ke saare kharche</li>
                        <li><b>Students_Report.xlsx</b> — Active students list</li>
                        <li><b>Bus_Fee_Dues_Report.xlsx</b> — Pending bus fees</li>
                    </ul>
                    <p>Naya academic year: <b>%s</b></p>
                    <br><p>— SmartSchool Team</p>
                    </body></html>
                    """.formatted(year, school.getSchoolName(),
                    getNextYear(year));

            helper.setText(body, true);

            // 5 Excel files attach karo
            helper.addAttachment(
                    "Attendance_Report_" + year + ".xlsx",
                    new ByteArrayResource(attendanceExcel),
                    "application/vnd.openxmlformats-" +
                            "officedocument.spreadsheetml.sheet");

            helper.addAttachment(
                    "Fees_Report_" + year + ".xlsx",
                    new ByteArrayResource(feesExcel),
                    "application/vnd.openxmlformats-" +
                            "officedocument.spreadsheetml.sheet");

            helper.addAttachment(
                    "Expenses_Report_" + year + ".xlsx",
                    new ByteArrayResource(expensesExcel),
                    "application/vnd.openxmlformats-" +
                            "officedocument.spreadsheetml.sheet");

            helper.addAttachment(
                    "Students_Report_" + year + ".xlsx",
                    new ByteArrayResource(studentsExcel),
                    "application/vnd.openxmlformats-" +
                            "officedocument.spreadsheetml.sheet");

            helper.addAttachment(
                    "Bus_Fee_Dues_Report_" + year + ".xlsx",
                    new ByteArrayResource(busFeeDuesExcel),
                    "application/vnd.openxmlformats-" +
                            "officedocument.spreadsheetml.sheet");

            mailSender.send(message);
            log.info("Year-end email sent to: {}", school.getMailId());

        } catch (Exception e) {
            log.error("Email FAILED for school '{}': {}",
                    school.getSchoolName(), e.getMessage());
            throw new RuntimeException("Email send failed: " + e.getMessage());
        }
    }

    private String getNextYear(String current) {
        // "2025-26" -> "2026-27"
        try {
            String[] parts = current.split("-");
            int start = Integer.parseInt(parts[0]) + 1;
            int end = Integer.parseInt(parts[1]) + 1;
            return start + "-" + end;
        } catch (Exception e) { return "Next Year"; }
    }
}