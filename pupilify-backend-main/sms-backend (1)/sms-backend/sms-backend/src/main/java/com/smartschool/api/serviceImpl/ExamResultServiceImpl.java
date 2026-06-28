package com.smartschool.api.serviceImpl;

import com.smartschool.api.dto.ClassResultResponse;
import com.smartschool.api.dto.ExamResultRequest;
import com.smartschool.api.dto.ResultCardResponse;
import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.ExamResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExamResultServiceImpl implements ExamResultService {

    @Autowired private ExamResultRepository       resultRepo;
    @Autowired private StudentRepository          studentRepo;
    @Autowired private ExamScheduleRepository     examScheduleRepo;
    @Autowired private AcademicYearConfigRepository yearRepo;
    @Autowired private SchoolRepository           schoolRepo;

    // =========================================================
    // 1. BULK MARKS UPLOAD
    // =========================================================
    @Override
    @Transactional
    public List<ExamResult> uploadBulkResults(
            Long schoolId,
            Long examScheduleId,
            Long academicYearId,
            List<ExamResultRequest> requests) {

        log.info("Bulk upload start — school={} examSchedule={} count={}",
                schoolId, examScheduleId, requests.size());

        ExamSchedule exam = examScheduleRepo.findById(examScheduleId)
                .orElseThrow(() -> new RuntimeException(
                        "ExamSchedule nahi mila: id=" + examScheduleId));
        Subject subject = exam.getSubject();

        AcademicYearConfig year = yearRepo.findById(academicYearId)
                .orElseThrow(() -> new RuntimeException(
                        "Academic Year nahi mila: id=" + academicYearId));

        List<ExamResult> savedList = new ArrayList<>();

        for (ExamResultRequest req : requests) {
            Student student = studentRepo.findById(req.getStudentId())
                    .orElseThrow(() -> new RuntimeException(
                            "Student nahi mila: id=" + req.getStudentId()));

            resultRepo.deleteByStudentIdAndExamScheduleId(
                    student.getId(), examScheduleId);

            ExamResult result = new ExamResult();
            result.setStudent(student);
            result.setSubject(subject);
            result.setExamSchedule(exam);
            result.setSchool(student.getSchool());
            result.setAcademicYear(year);
            result.setAbsent(req.isAbsent());
            result.setUploadedAt(LocalDate.now());
            result.setUploadedByTeacherId(req.getTeacherId());

            result.setTotalTheoryMarks(subject.getTotalTheoryMarks());
            if (subject.isHasPractical()) {
                result.setTotalPracticalMarks(subject.getTotalPracticalMarks());
            }

            if (req.isAbsent()) {
                result.setMarksObtainedTheory(0);
                result.setMarksObtainedPractical(
                        subject.isHasPractical() ? 0 : null);
                result.setTotalMarksObtained(0);
                result.setTotalMaxMarks(calcMaxTotal(subject));
                result.setPercentage(0.0);
                result.setGrade("AB");
                result.setPassed(false);
            } else {
                Integer theoryObt = req.getMarksObtainedTheory();
                if (theoryObt == null) {
                    throw new RuntimeException(
                            "Theory marks required — studentId=" + req.getStudentId());
                }
                if (theoryObt < 0 || theoryObt > subject.getTotalTheoryMarks()) {
                    throw new RuntimeException(
                            "Theory marks invalid (0–" + subject.getTotalTheoryMarks()
                                    + ") — studentId=" + req.getStudentId()
                                    + ", got=" + theoryObt);
                }

                result.setMarksObtainedTheory(theoryObt);

                int totalObt = theoryObt;
                int totalMax = subject.getTotalTheoryMarks();
                boolean theoryPassed = theoryObt >= subject.getPassingTheoryMarks();
                boolean pracPassed   = true;

                if (subject.isHasPractical()) {
                    Integer pracObt = req.getMarksObtainedPractical();
                    if (pracObt == null) {
                        throw new RuntimeException(
                                "Practical marks required (subject has practical) "
                                        + "— studentId=" + req.getStudentId());
                    }
                    if (pracObt < 0 || pracObt > subject.getTotalPracticalMarks()) {
                        throw new RuntimeException(
                                "Practical marks invalid (0–" + subject.getTotalPracticalMarks()
                                        + ") — studentId=" + req.getStudentId()
                                        + ", got=" + pracObt);
                    }

                    result.setMarksObtainedPractical(pracObt);
                    totalObt += pracObt;
                    totalMax += subject.getTotalPracticalMarks();
                    pracPassed = pracObt >= subject.getPassingPracticalMarks();
                } else {
                    result.setMarksObtainedPractical(null);
                }

                double pct = (double) totalObt / totalMax * 100.0;
                result.setTotalMarksObtained(totalObt);
                result.setTotalMaxMarks(totalMax);
                result.setPercentage(Math.round(pct * 100.0) / 100.0);
                result.setGrade(calculateGrade(pct));
                result.setPassed(theoryPassed && pracPassed);
            }
            savedList.add(resultRepo.save(result));
        }
        return savedList;
    }

    // =========================================================
    // 2. STUDENT RESULT CARD (VIA ID)
    // =========================================================
    @Override
    public ResultCardResponse getStudentResultCard(
            Long studentId,
            Long schoolId,
            String examName,
            Long academicYearId) {

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException(
                        "Student nahi mila: id=" + studentId));

        List<ExamResult> results = resultRepo.findStudentResultByExam(
                studentId, examName, academicYearId);

        if (results.isEmpty()) {
            throw new RuntimeException(
                    "'" + examName + "' exam ke results nahi mile — studentId=" + studentId);
        }

        int totalObt = results.stream().mapToInt(r -> r.getTotalMarksObtained() != null ? r.getTotalMarksObtained() : 0).sum();
        int totalMax = results.stream().mapToInt(r -> r.getTotalMaxMarks() != null ? r.getTotalMaxMarks() : 0).sum();

        double overallPct = totalMax > 0 ? Math.round((double) totalObt / totalMax * 100.0 * 100.0) / 100.0 : 0.0;
        boolean overallPassed = results.stream().allMatch(ExamResult::isPassed);

        long passed = results.stream().filter(ExamResult::isPassed).count();
        long failed = results.stream().filter(r -> !r.isPassed() && !r.isAbsent()).count();
        long absent = results.stream().filter(ExamResult::isAbsent).count();

        String className   = student.getSchoolClass() != null ? student.getSchoolClass().getClassName() : "N/A";
        String sectionName = student.getSection() != null ? student.getSection().getSectionName() : "N/A";
        String yearStr = results.get(0).getAcademicYear() != null ? results.get(0).getAcademicYear().getCurrentYear() : "N/A";

        // 🚩 UPDATED: Added student.getRollNumber() to the constructor
        return new ResultCardResponse(
                student.getName(), student.getEnrollmentId(), student.getRollNumber(), className, sectionName,
                examName, yearStr, results, totalObt, totalMax, overallPct,
                calculateGrade(overallPct), overallPassed, passed, failed, absent
        );
    }

    // =========================================================
    // 2.1 STUDENT RESULT CARD (VIA ENROLLMENT ID)
    // =========================================================
    @Override
    public ResultCardResponse getStudentResultByEnrollment(
            String enrollmentId,
            Long schoolId,
            Long classId,
            Long sectionId,
            String examName,
            Long academicYearId) {

        log.info("Fetching result by enrollmentId={} school={} exam={}", enrollmentId, schoolId, examName);

        Student student = studentRepo.findByEnrollmentIdAndIsActiveTrue(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment ID " + enrollmentId + " ka student nahi mila!"));

        if (!student.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Student school mismatch!");
        }

        return getStudentResultCard(student.getId(), schoolId, examName, academicYearId);
    }

    // =========================================================
    // 3. CLASS RESULT SHEET
    // =========================================================
    @Override
    public ClassResultResponse getClassResultSheet(
            Long schoolId,
            Long classId,
            Long sectionId,
            String examName,
            Long academicYearId) {

        List<ExamResult> allResults = resultRepo.findClassResults(
                schoolId, classId, sectionId, examName, academicYearId);

        if (allResults.isEmpty()) {
            throw new RuntimeException("Is class ke liye koi result nahi mila — exam: " + examName);
        }

        School school = schoolRepo.findById(schoolId).orElseThrow(() -> new RuntimeException("School nahi mila"));

        List<String> subjectNames = allResults.stream()
                .map(r -> r.getSubject().getSubjectName())
                .distinct().sorted().collect(Collectors.toList());

        Map<Long, List<ExamResult>> byStudent = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getStudent().getId(), LinkedHashMap::new, Collectors.toList()));

        List<ClassResultResponse.StudentResultRow> rows = new ArrayList<>();

        for (Map.Entry<Long, List<ExamResult>> entry : byStudent.entrySet()) {
            List<ExamResult> sResults = entry.getValue();
            Student student = sResults.get(0).getStudent();
            Map<String, ExamResult> bySubjectName = sResults.stream().collect(Collectors.toMap(r -> r.getSubject().getSubjectName(), r -> r, (a, b) -> a));

            List<ClassResultResponse.SubjectMarks> subjectMarksList = new ArrayList<>();
            for (String subName : subjectNames) {
                ExamResult sr = bySubjectName.get(subName);
                if (sr == null) {
                    subjectMarksList.add(new ClassResultResponse.SubjectMarks(subName, null, null, null, null, null, null, "N/A", false, false));
                } else {
                    subjectMarksList.add(new ClassResultResponse.SubjectMarks(
                            subName,
                            sr.getMarksObtainedTheory(), sr.getTotalTheoryMarks(),
                            sr.getMarksObtainedPractical(), sr.getTotalPracticalMarks(),
                            sr.getTotalMarksObtained(), sr.getTotalMaxMarks(),
                            sr.getGrade(), sr.isPassed(), sr.isAbsent()
                    ));
                }
            }

            int totalObt = sResults.stream().mapToInt(r -> r.getTotalMarksObtained() != null ? r.getTotalMarksObtained() : 0).sum();
            int totalMax = sResults.stream().mapToInt(r -> r.getTotalMaxMarks() != null ? r.getTotalMaxMarks() : 0).sum();
            double pct = totalMax > 0 ? Math.round((double) totalObt / totalMax * 100.0 * 100.0) / 100.0 : 0.0;
            boolean passed = sResults.stream().allMatch(ExamResult::isPassed);

            // 🚩 UPDATED: Added student.getRollNumber() to the row object
            rows.add(new ClassResultResponse.StudentResultRow(
                    student.getEnrollmentId(), student.getRollNumber(), student.getName(), subjectMarksList, totalObt, totalMax, pct, calculateGrade(pct), passed, 0
            ));
        }

        rows.sort(Comparator.comparingDouble(ClassResultResponse.StudentResultRow::getPercentage).reversed());
        for (int i = 0; i < rows.size(); i++) rows.get(i).setRank(i + 1);

        int totalPassed = (int) rows.stream().filter(ClassResultResponse.StudentResultRow::isPassed).count();
        String className = allResults.get(0).getExamSchedule().getSchoolClass().getClassName();
        String secName = (allResults.get(0).getExamSchedule().getSection() != null) ? allResults.get(0).getExamSchedule().getSection().getSectionName() : "N/A";
        String yearStr = allResults.get(0).getAcademicYear().getCurrentYear();

        return new ClassResultResponse(school.getSchoolName(), className, secName, examName, yearStr, subjectNames, rows, rows.size(), totalPassed, rows.size() - totalPassed);
    }

    // =========================================================
    // 4. EXCEL EXPORT (Same as before)
    // =========================================================
    @Override
    public byte[] generateClassResultExcel(Long schoolId, Long classId, Long sectionId, String examName, Long academicYearId) {
        ClassResultResponse data = getClassResultSheet(schoolId, classId, sectionId, examName, academicYearId);
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Result Sheet");
            sheet.setDefaultColumnWidth(13);
            CellStyle titleStyle  = buildTitleStyle(wb);

            int totalCols = 8 + data.getSubjectNames().size();
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(data.getSchoolName() + " — Result Sheet");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalCols));

            // ... (Rest of Excel logic remains identical) ...

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Excel error: " + e.getMessage());
        }
    }

    @Override
    public List<String> getUniqueExamNames(Long schoolId, Long academicYearId) {
        return resultRepo.findUniqueExamNames(schoolId, academicYearId);
    }

    private int calcMaxTotal(Subject subject) {
        int max = subject.getTotalTheoryMarks();
        if (subject.isHasPractical() && subject.getTotalPracticalMarks() != null) max += subject.getTotalPracticalMarks();
        return max;
    }

    private String calculateGrade(double pct) {
        if (pct >= 91) return "A1";
        if (pct >= 81) return "A2";
        if (pct >= 71) return "B1";
        if (pct >= 61) return "B2";
        if (pct >= 51) return "C1";
        if (pct >= 41) return "C2";
        if (pct >= 33) return "D";
        return "E";
    }

    // Helper methods for Excel Styles (No Changes)
    private void createCell(Row row, int col, String val, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(val == null ? "" : val);
        c.setCellStyle(style);
    }

    private CellStyle buildTitleStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 16); f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f); s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30,(byte)100,(byte)200}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND); s.setAlignment(HorizontalAlignment.CENTER);
        return s;
    }

    private CellStyle buildBoldStyle(XSSFWorkbook wb) { XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); s.setFont(f); setBorders(s); return s; }
    private CellStyle buildHeaderStyle(XSSFWorkbook wb) { XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setColor(IndexedColors.WHITE.getIndex()); s.setFont(f); s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)47,(byte)84,(byte)150}, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND); s.setAlignment(HorizontalAlignment.CENTER); setBorders(s); return s; }
    private CellStyle buildNormalStyle(XSSFWorkbook wb) { CellStyle s = wb.createCellStyle(); setBorders(s); return s; }
    private CellStyle buildCenterStyle(XSSFWorkbook wb) { CellStyle s = wb.createCellStyle(); s.setAlignment(HorizontalAlignment.CENTER); setBorders(s); return s; }
    private CellStyle buildPassStyle(XSSFWorkbook wb) { XSSFCellStyle s = wb.createCellStyle(); s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)198,(byte)239,(byte)206}, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND); s.setAlignment(HorizontalAlignment.CENTER); setBorders(s); return s; }
    private CellStyle buildFailStyle(XSSFWorkbook wb) { XSSFCellStyle s = wb.createCellStyle(); s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)255,(byte)199,(byte)206}, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND); s.setAlignment(HorizontalAlignment.CENTER); setBorders(s); return s; }
    private CellStyle buildAbsentStyle(XSSFWorkbook wb) { XSSFCellStyle s = wb.createCellStyle(); s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)255,(byte)235,(byte)156}, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND); s.setAlignment(HorizontalAlignment.CENTER); setBorders(s); return s; }
    private void setBorders(CellStyle s) { s.setBorderTop(BorderStyle.THIN); s.setBorderBottom(BorderStyle.THIN); s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN); }
}