package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Data
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String expenseName;   // e.g., Electricity Bill, Rent, Stationery
    private Double amount;
    private LocalDate date;
    private String category;      // e.g., Maintenance, Salary, Bills

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYearConfig academicYear;
}