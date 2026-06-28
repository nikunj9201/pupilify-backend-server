package com.smartschool.api.security;

import com.smartschool.api.entity.User;
import com.smartschool.api.entity.StateManager;
import com.smartschool.api.entity.DistrictManager;

import com.smartschool.api.repository.UserRepository;
import com.smartschool.api.repository.StateManagerRepository;
import com.smartschool.api.repository.DistrictManagerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StateManagerRepository stateManagerRepository;

    @Autowired
    private DistrictManagerRepository districtManagerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // ================= 1. NORMAL USER =================
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            String role = formatRole(user.getRole().name());
            System.out.println("🔐 Loading user: " + username + ", Role: " + role);

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority(role))
            );
        }

        // ================= 2. STATE MANAGER =================
        var smOpt = stateManagerRepository.findByEmail(username);
        if (smOpt.isPresent()) {
            StateManager sm = smOpt.get();
            
            String role = formatRole(sm.getRole());
            System.out.println("🔐 Loading state manager: " + username + ", Role: " + role);

            return new org.springframework.security.core.userdetails.User(
                    sm.getEmail(),
                    sm.getPassword(),
                    List.of(new SimpleGrantedAuthority(role))
            );
        }

        // ================= 3. DISTRICT MANAGER =================
        var dmOpt = districtManagerRepository.findByEmail(username);
        if (dmOpt.isPresent()) {
            DistrictManager dm = dmOpt.get();
            
            String role = formatRole(dm.getRole());
            System.out.println("🔐 Loading district manager: " + username + ", Role: " + role);

            return new org.springframework.security.core.userdetails.User(
                    dm.getEmail(),
                    dm.getPassword(),
                    List.of(new SimpleGrantedAuthority(role))
            );
        }

        // ================= NOT FOUND =================
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    // ================= 🔥 ROLE FORMAT FIX =================
    private String formatRole(String role) {
        if (role == null) return "ROLE_USER";

        // agar already ROLE_ hai to waise hi return
        if (role.startsWith("ROLE_")) {
            return role;
        }

        // warna prefix add karo
        return "ROLE_" + role;
    }
}