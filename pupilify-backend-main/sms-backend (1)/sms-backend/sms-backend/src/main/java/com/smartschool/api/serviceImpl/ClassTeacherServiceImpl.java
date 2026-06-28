package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.*;
import com.smartschool.api.repository.*;
import com.smartschool.api.service.ClassTeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClassTeacherServiceImpl implements ClassTeacherService {

    private static final Logger log = LoggerFactory.getLogger(ClassTeacherServiceImpl.class);

    @Autowired private ClassTeacherRepository mappingRepo;
    @Autowired private TeacherRepository teacherRepo;
    @Autowired private SectionRepository sectionRepo;
    @Autowired private ClassRepository classRepo;
    @Autowired private SchoolRepository schoolRepo;
    @Autowired private AcademicYearConfigRepository yearRepo;

    @Override
    @Transactional
    public ClassTeacherMapping assignClassTeacher(Long classId, Long sectionId, Long teacherId, Long schoolId, Long academicYearId) {
        log.info("Assigning class teacher: teacherId={} classId={} sectionId={} yearId={}", teacherId, classId, sectionId, academicYearId);

        // ✅ Validation using Year ID
        if (mappingRepo.existsByTeacherIdAndAcademicYearIdAndIsActiveTrue(teacherId, academicYearId)) {
            throw new RuntimeException("Teacher is already an active class teacher for this academic year.");
        }

        if (sectionId != null && sectionId > 0) {
            if (mappingRepo.existsBySectionIdAndAcademicYearIdAndIsActiveTrue(sectionId, academicYearId)) {
                throw new RuntimeException("This section already has an active class teacher.");
            }
        } else {
            if (mappingRepo.existsBySchoolClassIdAndSectionIsNullAndAcademicYearIdAndIsActiveTrue(classId, academicYearId)) {
                throw new RuntimeException("This class already has an active class teacher.");
            }
        }

        // 🚩 Fetch Entity Object to avoid "Incompatible Types" error
        AcademicYearConfig yearConfig = yearRepo.findById(academicYearId)
                .orElseThrow(() -> new RuntimeException("Academic Year not found"));

        ClassTeacherMapping mapping = new ClassTeacherMapping();
        mapping.setSchoolClass(classRepo.findById(classId).orElseThrow(() -> new RuntimeException("Class not found")));
        mapping.setTeacher(teacherRepo.findById(teacherId).orElseThrow(() -> new RuntimeException("Teacher not found")));
        mapping.setSchool(schoolRepo.findById(schoolId).orElseThrow(() -> new RuntimeException("School not found")));

        // ✅ CORRECT: Entity object set kiya ja raha hai, String nahi
        mapping.setAcademicYear(yearConfig);

        if (sectionId != null && sectionId > 0) {
            mapping.setSection(sectionRepo.findById(sectionId).orElseThrow());
        } else {
            mapping.setSection(null);
        }
        mapping.setActive(true);
        return mappingRepo.save(mapping);
    }

    @Override
    @Transactional
    public void deleteMapping(Long id) {
        ClassTeacherMapping mapping = mappingRepo.findById(id).orElseThrow();
        mapping.setActive(false);
        mappingRepo.save(mapping);
    }

    // 🚩 FIXED: 'classTeacherMappingRepository' ki jagah 'mappingRepo' use kiya hai
    @Override
    public List<ClassTeacherMapping> getMappingsByTeacher(Long teacherId) {
        log.info("Fetching mappings for teacherId: {}", teacherId);
        // Humne upar @Autowired mein naam 'mappingRepo' diya hai, isliye yahan wahi use hoga
        return mappingRepo.findByTeacherIdAndIsActiveTrue(teacherId);
    }

    @Override
    public List<ClassTeacherMapping> getMappingsBySchool(Long schoolId) {
        return mappingRepo.findBySchoolIdAndIsActiveTrue(schoolId);
    }

    @Override
    public List<ClassTeacherMapping> getMappingsBySchoolAndYear(Long schoolId, Long academicYearId) {
        log.debug("Fetching mappings for schoolId: {} and academicYearId: {}", schoolId, academicYearId);
        return mappingRepo.findBySchoolIdAndAcademicYearIdAndIsActiveTrue(schoolId, academicYearId);
    }
}