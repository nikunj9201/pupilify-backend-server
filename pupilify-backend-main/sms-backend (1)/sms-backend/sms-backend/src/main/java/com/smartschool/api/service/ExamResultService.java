package com.smartschool.api.service;

import com.smartschool.api.dto.ClassResultResponse;
import com.smartschool.api.dto.ExamResultRequest;
import com.smartschool.api.dto.ResultCardResponse;
import com.smartschool.api.entity.ExamResult;
import java.util.List;

public interface ExamResultService {

    /**
     * Bulk marks upload — ek exam schedule ke liye
     * ek class ke saare students ke marks ek saath save karo.
     */
    List<ExamResult> uploadBulkResults(
            Long schoolId,
            Long examScheduleId,
            Long academicYearId,
            List<ExamResultRequest> requests);

    /**
     * Student ka ek exam ka poora result card via Student ID.
     */
    ResultCardResponse getStudentResultCard(
            Long studentId,
            Long schoolId,
            String examName,
            Long academicYearId);

    /**
     * 🚩 NAYA FEATURE: Student result card via ENROLLMENT ID
     * @param enrollmentId   Student ka unique enrollment ID (e.g. STU-2026-001)
     * @param schoolId       School id
     * @param classId        Class id (Optional security filter)
     * @param sectionId      Section id (Optional)
     * @param examName       e.g. "Half Yearly"
     * @param academicYearId Academic year id
     * @return ResultCardResponse jisme saare subjects ke marks honge
     */
    ResultCardResponse getStudentResultByEnrollment(
            String enrollmentId, // 🚩 FIXED: Enrollment ID (String) use ho raha hai
            Long schoolId,
            Long classId,
            Long sectionId,
            String examName,
            Long academicYearId);

    /**
     * Ek class ka poora result sheet — saare students, saare subjects.
     */
    ClassResultResponse getClassResultSheet(
            Long schoolId,
            Long classId,
            Long sectionId,
            String examName,
            Long academicYearId);

    /**
     * Excel file generate karo — class result sheet.
     */
    byte[] generateClassResultExcel(
            Long schoolId,
            Long classId,
            Long sectionId,
            String examName,
            Long academicYearId);

    /**
     * School mein jo bhi exams ho chuke hain unke unique names.
     */
    List<String> getUniqueExamNames(
            Long schoolId,
            Long academicYearId);
}