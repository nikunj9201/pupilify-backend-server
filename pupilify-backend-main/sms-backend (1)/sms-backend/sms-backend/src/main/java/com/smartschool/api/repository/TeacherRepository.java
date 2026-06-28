package com.smartschool.api.repository;

import com.smartschool.api.entity.Teacher;
import com.smartschool.api.entity.User; // Zaroori Import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // Zaroori Import
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    // School ID ke basis par teachers ki list nikalne ke liye
    List<Teacher> findBySchoolId(Long schoolId);

    // User object se teacher profile nikalne ke liye (Login logic)
    Optional<Teacher> findByUser(User user);

    @Modifying
    @Query("UPDATE Teacher t SET t.active = false WHERE t.school.id = :schoolId")
    void deactivateTeachersBySchoolId(Long schoolId);
}