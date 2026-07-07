package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BusServiceImpl implements BusService {

    @Autowired
    private StudentBusAssignmentRepository assignmentRepository;

    @Autowired
    private BusFeePaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StoppageRepository stoppageRepository;

    @Autowired
    private AcademicYearConfigRepository academicYearRepository;

    @Override
    public StudentBusAssignment assignStudentToBus(Long studentId, Long stoppageId, Long academicYearId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        Stoppage stoppage = stoppageRepository.findById(stoppageId).orElseThrow(() -> new RuntimeException("Stoppage not found"));
        AcademicYearConfig academicYear = academicYearRepository.findById(academicYearId).orElseThrow(() -> new RuntimeException("Academic year not found"));

        assignmentRepository.findByStudentIdAndAcademicYearIdAndIsActiveTrue(studentId, academicYearId).ifPresent(assignment -> {
            assignment.setActive(false);
            assignmentRepository.save(assignment);
        });

        StudentBusAssignment newAssignment = new StudentBusAssignment();
        newAssignment.setStudent(student);
        newAssignment.setStoppage(stoppage);
        newAssignment.setAcademicYear(academicYear);
        newAssignment.setTransportFee(stoppage.getFee());
        return assignmentRepository.save(newAssignment);
    }

    @Override
    public BusFeePayment collectBusFee(Long studentId, Long academicYearId, double amount, String paymentMode) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        AcademicYearConfig academicYear = academicYearRepository.findById(academicYearId).orElseThrow(() -> new RuntimeException("Academic year not found"));

        BusFeePayment payment = new BusFeePayment();
        payment.setStudent(student);
        payment.setAcademicYear(academicYear);
        payment.setAmountPaid(amount);
        payment.setPaymentMode(paymentMode);
        payment.setReceiptNumber("BUS-RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return paymentRepository.save(payment);
    }

    @Override
    public List<Map<String, Object>> getBusFeeDueReport(Long schoolId, Long academicYearId) {
        List<StudentBusAssignment> assignments = assignmentRepository.findByStudent_School_IdAndAcademicYearIdAndIsActiveTrue(schoolId, academicYearId);
        List<Map<String, Object>> report = new ArrayList<>();

        for (StudentBusAssignment assignment : assignments) {
            List<BusFeePayment> payments = paymentRepository.findByStudentIdAndAcademicYearId(assignment.getStudent().getId(), academicYearId);
            double totalPaid = payments.stream().mapToDouble(BusFeePayment::getAmountPaid).sum();
            double due = assignment.getTransportFee() - totalPaid;

            if (due > 0) {
                Map<String, Object> row = new HashMap<>();
                row.put("studentName", assignment.getStudent().getName());
                row.put("enrollmentId", assignment.getStudent().getEnrollmentId());
                row.put("className", assignment.getStudent().getSchoolClass().getClassName());
                row.put("sectionName", assignment.getStudent().getSection() != null ? assignment.getStudent().getSection().getSectionName() : "N/A");
                row.put("stoppage", assignment.getStoppage().getStopName());
                row.put("totalFee", assignment.getTransportFee());
                row.put("totalPaid", totalPaid);
                row.put("dueAmount", due);
                report.add(row);
            }
        }
        return report;
    }
}