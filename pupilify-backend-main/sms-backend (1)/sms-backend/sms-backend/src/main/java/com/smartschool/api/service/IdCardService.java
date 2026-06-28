package com.smartschool.api.service;

/**
 * ID Card generation service.
 * HTML bytes return karta hai — frontend browser mein render karke print/save kar sakta hai.
 */
public interface IdCardService {

    /**
     * Student ka ID Card HTML generate karta hai via Enrollment ID.
     * 🚩 UPDATED: studentId (Long) ki jagah enrollmentId (String) use ho raha hai.
     * @param enrollmentId Student ka unique enrollment ID (e.g. STU-2026-001)
     * @param schoolId     School ka ID (validation ke liye)
     * @return             ID Card HTML as byte[]
     */
    byte[] generateStudentIdCard(String enrollmentId, Long schoolId);

    /**
     * Teacher ka ID Card HTML generate karta hai.
     * @param teacherId  Teacher ka DB ID
     * @param schoolId   School ka ID (validation ke liye)
     * @return           ID Card HTML as byte[]
     */
    byte[] generateTeacherIdCard(Long teacherId, Long schoolId);

    /**
     * Ek class ke saare students ke ID cards generate karta hai (bulk).
     * @param schoolId   School ID
     * @param classId    Class ID
     * @param sectionId  Section ID (0 = no section)
     * @return           Saare cards ka combined HTML as byte[]
     */
    byte[] generateBulkStudentIdCards(Long schoolId, Long classId, Long sectionId);
}