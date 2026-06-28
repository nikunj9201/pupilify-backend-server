package com.smartschool.api.repository;

import com.smartschool.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // UserRepository.java mein ye line add karein
// UserRepository.java
    List<User> findBySchoolId(Long schoolId);
    // 1. Login ke liye username se user dhundna (Yahi use hoga email ke liye bhi)
    Optional<User> findByUsername(String username);

    // 🚩 NOTE: findByEmail yahan se hata diya gaya hai kyunki User Entity mein
    // 'email' naam ka column nahi hai. Hum 'username' hi use karenge.

    // 2. DataLoader aur validation ke liye
    boolean existsByUsername(String username);

    // 3. School ke basis par saare users ka active status update karna
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = :status WHERE u.school.id = :schoolId")
    void updateUserStatusBySchoolId(@Param("schoolId") Long schoolId, @Param("status") boolean status);

    @Modifying
    @Query("UPDATE User u SET u.username = :newEmail WHERE u.school.id = :schoolId")
    void updateUsernameBySchoolId(Long schoolId, String newEmail);

    @Modifying
    @Query("UPDATE User u SET u.username = :deletedEmail, u.active = false WHERE u.school.id = :schoolId")
    void softDeleteUsersBySchoolId(Long schoolId, String deletedEmail);

    @Modifying
    @Query("UPDATE User u SET u.username = CONCAT('deleted_', :timestamp, '_', u.username), u.active = false WHERE u.school.id = :schoolId")
    void deactivateAllUsersBySchoolId(Long schoolId, String timestamp);

    // 🚩 NAYA: Department login ke liye
    Optional<User> findByDepartmentId(Long departmentId);
    Optional<User> findByDepartment(String department);
    List<User> findBySchoolIdAndDepartment(Long schoolId, String department);
}

