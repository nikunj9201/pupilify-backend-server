package com.smartschool.api.repository;

import com.smartschool.api.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // School aur Year wise kharche [cite: 130]
    List<Expense> findBySchoolIdAndAcademicYearId(Long schoolId, Long yearId);

    // Total Expenses ka sum nikalne ke liye query [cite: 109]
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.school.id = :schoolId AND e.academicYear.id = :yearId")
    Double getTotalExpensesBySchool(@Param("schoolId") Long schoolId, @Param("yearId") Long yearId);
}