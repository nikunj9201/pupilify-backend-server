package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.BusFeeService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusFeeServiceImpl implements BusFeeService {

    @Autowired
    private BusFeeStructureRepository structureRepository;

    @Autowired
    private BusFeePaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentBusAssignmentRepository assignmentRepository;

    @Override
    public List<BusFeeStructure> createBulkFeeStructure(Long schoolId, Long busId, List<BusFeeStructure> feeStructures) {
        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new RuntimeException("School not found"));
        Bus bus = busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found"));

        for (BusFeeStructure structure : feeStructures) {
            structure.setSchool(school);
            structure.setBus(bus);
        }
        return structureRepository.saveAll(feeStructures);
    }

    @Override
    public BusFeeStructure updateFeeStructure(Long structureId, double amount) {
        BusFeeStructure structure = structureRepository.findById(structureId).orElseThrow(() -> new RuntimeException("Fee structure not found"));
        structure.setAmount(amount);
        return structureRepository.save(structure);
    }

    @Override
    public void deleteFeeStructure(Long structureId) {
        structureRepository.deleteById(structureId);
    }

    @Override
    public BusFeePayment collectFee(String studentIdentifier, double amount, String paymentMode) {
        Student student = studentRepository.findByEnrollmentIdOrId(studentIdentifier, Long.parseLong(studentIdentifier))
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<BusFeeStructure> feeStructure = getStudentFeeStructure(student.getId());
        List<BusFeePayment> payments = paymentRepository.findByStudentId(student.getId());

        double totalPaid = payments.stream().mapToDouble(BusFeePayment::getAmountPaid).sum();
        double totalDue = feeStructure.stream().mapToDouble(BusFeeStructure::getAmount).sum();

        if (totalPaid >= totalDue) {
            throw new RuntimeException("No due fees for this student");
        }

        BusFeeStructure nextDueMonth = feeStructure.stream()
                .filter(fs -> payments.stream().noneMatch(p -> p.getBusFeeStructure().getId().equals(fs.getId())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No due fees for this student"));

        BusFeePayment payment = new BusFeePayment();
        payment.setStudent(student);
        payment.setBusFeeStructure(nextDueMonth);
        payment.setAmountPaid(amount);
        payment.setPaymentMode(paymentMode);
        payment.setReceiptNumber("BUS-RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return paymentRepository.save(payment);
    }

    @Override
    public List<Map<String, Object>> getDueReport(Long busId) {
        List<StudentBusAssignment> assignments = assignmentRepository. findAllByStoppage_Route_Bus_Id(busId);
        List<Map<String, Object>> report = new ArrayList<>();

        for (StudentBusAssignment assignment : assignments) {
            Student student = assignment.getStudent();
            List<BusFeeStructure> feeStructure = getStudentFeeStructure(student.getId());
            List<BusFeePayment> payments = paymentRepository.findByStudentId(student.getId());

            List<Long> paidStructureIds = payments.stream().map(p -> p.getBusFeeStructure().getId()).collect(Collectors.toList());
            List<BusFeeStructure> dueStructure = feeStructure.stream()
                    .filter(fs -> !paidStructureIds.contains(fs.getId()))
                    .collect(Collectors.toList());

            if (!dueStructure.isEmpty()) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentId", student.getId());
                row.put("studentName", student.getName());
                row.put("enrollmentId", student.getEnrollmentId());
                row.put("dueMonths", dueStructure.stream().map(fs -> fs.getMonth() + " " + fs.getYear()).collect(Collectors.toList()));
                row.put("totalDueAmount", dueStructure.stream().mapToDouble(BusFeeStructure::getAmount).sum());
                report.add(row);
            }
        }
        return report;
    }

    @Override
    public List<BusFeeStructure> getStudentFeeStructure(Long studentId) {
        StudentBusAssignment assignment = assignmentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student is not assigned to any bus route"));
        return structureRepository.findByBusId(assignment.getStoppage().getRoute().getBus().getId());
    }

    @Override
    public byte[] generateBusFeeDueReportExcel(Long schoolId, Long academicYearId) throws IOException {
        List<Map<String, Object>> dueReport = getDueReportBySchool(schoolId, academicYearId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bus Fee Dues");

        String[] headers = {"Student Name", "Enrollment ID", "Total Due Amount"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Map<String, Object> reportRow : dueReport) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) reportRow.get("studentName"));
            row.createCell(1).setCellValue((String) reportRow.get("enrollmentId"));
            row.createCell(2).setCellValue((Double) reportRow.get("totalDueAmount"));
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    private List<Map<String, Object>> getDueReportBySchool(Long schoolId, Long academicYearId) {
        List<Student> students = studentRepository.findBySchoolIdAndIsActiveTrue(schoolId);
        List<Map<String, Object>> report = new ArrayList<>();

        for (Student student : students) {
            try {
                List<BusFeeStructure> feeStructure = getStudentFeeStructure(student.getId());
                List<BusFeePayment> payments = paymentRepository.findByStudentId(student.getId());

                List<Long> paidStructureIds = payments.stream().map(p -> p.getBusFeeStructure().getId()).collect(Collectors.toList());
                List<BusFeeStructure> dueStructure = feeStructure.stream()
                        .filter(fs -> !paidStructureIds.contains(fs.getId()))
                        .collect(Collectors.toList());

                if (!dueStructure.isEmpty()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("studentName", student.getName());
                    row.put("enrollmentId", student.getEnrollmentId());
                    row.put("totalDueAmount", dueStructure.stream().mapToDouble(BusFeeStructure::getAmount).sum());
                    report.add(row);
                }
            } catch (Exception e) {
                // Ignore students not assigned to a bus
            }
        }
        return report;
    }
}