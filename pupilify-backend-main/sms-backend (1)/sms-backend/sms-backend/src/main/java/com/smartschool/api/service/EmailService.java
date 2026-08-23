package com.smartschool.api.service;

import com.smartschool.api.entity.School;

public interface EmailService {

    /**
     * Year-end par school ki mail ID par 5 Excel files bheji jaati hain:
     * 1. Attendance Report
     * 2. Fees Report
     * 3. Expenses Report
     * 4. Students Report
     * 5. Bus Fee Dues Report
     */
    void sendYearEndDataEmail(
            School school,
            String year,
            byte[] attendanceExcel,
            byte[] feesExcel,
            byte[] expensesExcel,
            byte[] studentsExcel,
            byte[] busFeeDuesExcel);
}