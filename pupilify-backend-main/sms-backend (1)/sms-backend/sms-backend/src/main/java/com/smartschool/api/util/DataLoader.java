package com.smartschool.api.util;

import com.smartschool.api.entity.Role;
import com.smartschool.api.entity.User;
import com.smartschool.api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Super Admin check and creation
            String superAdminEmail = "nikunjpatidar8888@gmail.com";

            // Check if table exists and super admin user exists
            if (!userRepository.existsByUsername(superAdminEmail)) {
                User superAdmin = new User();
                superAdmin.setUsername(superAdminEmail);
                superAdmin.setPassword(passwordEncoder.encode("Nikunj8888@"));
                superAdmin.setRole(Role.ROLE_SUPER_ADMIN);
                superAdmin.setSchool(null);
                superAdmin.setActive(true); // ✅ Super Admin hamesha active rahega
                userRepository.save(superAdmin);
                log.info("Super Admin created successfully!");
            } else {
                log.info("Super Admin already exists!");
            }
        } catch (Exception e) {
            log.warn("DataLoader: Could not load initial data - Tables might not be created yet. " +
                    "This is normal during first startup. Error: {}", e.getMessage());
            // Don't fail startup - tables will be created by Hibernate
        }
    }
}

