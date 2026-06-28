package com.smartschool.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * StudentFeeDue — Purane saal ki unpaid fees track karne ke liye.
 *
 * Kab banta hai:
 *   → Jab academic year change hota hai aur student ki fees poori nahi bhari hoti
 *   → savePendingDues() method is record ko banata hai DELETE se pehle
 *
 * Kab clear hota hai:
 *   → Jab student naye saal mein purani due bhar deta hai
 *   → POST /api/admin/fees/dues/pay/{dueId}/{schoolId}
 */
@Entity
@Table(name = "student_fee_dues")
@Data
public class StudentFeeDue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Purana year jis mein due hua — "2025-26"
    private String fromYear;

    // Naya year jis mein carry forward hua — "2026-27"
    private String toYear;

    // Us saal ki total fees thi
    private Double totalFees;

    // Student ne kitna bhara tha us saal
    private Double totalPaid;

    // Kitna baaki tha (totalFees - totalPaid)
    private Double dueAmount;

    // Naye saal mein kitna bhar chuka hai (start: 0.0)
    private Double paidFromDue;

    // Abhi bhi kitna baaki hai (start: dueAmount, ghatta hai jab pay karo)
    private Double remainingDue;

    // true = poori due clear ho gayi
    private boolean cleared;

    private LocalDate createdAt;

    // ─── Relations ───────────────────────────────────────────

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    // Purana year config (jis saal due hua)
    @ManyToOne
    @JoinColumn(name = "from_academic_year_id")
    private AcademicYearConfig fromAcademicYear;

    // Naya year config (jis saal carry forward hua)
    @ManyToOne
    @JoinColumn(name = "to_academic_year_id")
    private AcademicYearConfig toAcademicYear;

    @PrePersist
    protected void onCreate() {
        this.createdAt  = LocalDate.now();
        this.cleared    = false;
        if (this.paidFromDue  == null) this.paidFromDue  = 0.0;
        if (this.remainingDue == null) this.remainingDue = this.dueAmount;
    }
}