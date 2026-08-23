package com.smartschool.api.dto;

import com.smartschool.api.entity.Student;
import lombok.Data;

@Data
public class StudentExcelDTO {
    private Long id;
    private String enrollmentId;
    private Integer rollNumber;
    private String name;
    private String gender;
    private String phoneNo;
    private String email;
    private String address;
    private String dob;
    private String fatherName;
    private String motherName;
    private String fatherContactNumber;
    private String caste;
    private String apaarId;
    private String aadharCardNo;
    private String samagraId;
    private String familyId;
    private String scholarNo;
    private String fatherOccupation;
    private String fatherSalary;
    private String postalCode;
    private String bankName;
    private String bankAccountNo;
    private String ifscCode;
    private String branch;
    private String penNumber;
    private String className;
    private String sectionName;
    private String academicYear;

    public static StudentExcelDTO fromEntity(Student student) {
        StudentExcelDTO dto = new StudentExcelDTO();
        dto.setId(student.getId());
        dto.setEnrollmentId(student.getEnrollmentId());
        dto.setRollNumber(student.getRollNumber());
        dto.setName(student.getName());
        dto.setGender(student.getGender());
        dto.setPhoneNo(student.getPhoneNo());
        dto.setEmail(student.getEmail());
        dto.setAddress(student.getAddress());
        dto.setDob(student.getDob());
        dto.setFatherName(student.getFatherName());
        dto.setMotherName(student.getMotherName());
        dto.setFatherContactNumber(student.getFatherContactNumber());
        dto.setCaste(student.getCaste());
        dto.setApaarId(student.getApaarId());
        dto.setAadharCardNo(student.getAadharCardNo());
        dto.setSamagraId(student.getSamagraId());
        dto.setFamilyId(student.getFamilyId());
        dto.setScholarNo(student.getScholarNo());
        dto.setFatherOccupation(student.getFatherOccupation());
        dto.setFatherSalary(student.getFatherSalary());
        dto.setPostalCode(student.getPostalCode());
        dto.setBankName(student.getBankName());
        dto.setBankAccountNo(student.getBankAccountNo());
        dto.setIfscCode(student.getIfscCode());
        dto.setBranch(student.getBranch());
        dto.setPenNumber(student.getPenNumber());
        dto.setClassName(student.getSchoolClass() != null ? student.getSchoolClass().getClassName() : "N/A");
        dto.setSectionName(student.getSection() != null ? student.getSection().getSectionName() : "N/A");
        dto.setAcademicYear(student.getAcademicYear() != null ? student.getAcademicYear().getCurrentYear() : "N/A");
        return dto;
    }
}