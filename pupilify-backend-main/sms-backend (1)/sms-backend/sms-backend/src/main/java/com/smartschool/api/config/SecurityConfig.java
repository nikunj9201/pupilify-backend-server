package com.smartschool.api.config;

import com.smartschool.api.security.JwtAuthenticationEntryPoint;
import com.smartschool.api.security.JwtAuthenticationFilter;
import com.smartschool.api.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil, UserDetailsService userDetailsService) throws Exception {

        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setExposedHeaders(List.of("Authorization", "New-Token"));
                    return config;
                }))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC APIs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/auth/forgot-password").permitAll()
                        .requestMatchers("/api/auth/reset-password").permitAll()
                        .requestMatchers("/api/department-login/**").permitAll()

                        // 2. SPECIFIC PATTERNS FIRST (Must be before generic /api/admin/**)
                        // FEES APIs - Allow ADMIN, PRINCIPAL, TEACHER, STUDENT, and DEPARTMENT roles
                        .requestMatchers(HttpMethod.POST, "/api/admin/fees/adjustments/add/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/admin/fees/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "TEACHER", "STUDENT", "DEPARTMENT")
                        // ATTENDANCE APIs - Allow ADMIN, PRINCIPAL, TEACHER, STUDENT, and DEPARTMENT roles
                        .requestMatchers("/api/admin/attendance/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "TEACHER", "STUDENT", "DEPARTMENT")
                        // TIMETABLE APIs - Allow ADMIN, PRINCIPAL, TEACHER, STUDENT, and DEPARTMENT roles
                        .requestMatchers("/api/admin/timetable/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "TEACHER", "STUDENT", "DEPARTMENT")
                        // EXAMS APIs - Allow ADMIN, PRINCIPAL, TEACHER, STUDENT, and DEPARTMENT roles (students need to see exam schedule)
                        .requestMatchers("/api/admin/exams/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "TEACHER", "STUDENT", "DEPARTMENT")
                        // RESULTS APIs - Allow ADMIN, PRINCIPAL, TEACHER, STUDENT, and DEPARTMENT roles (students need to see their results)
                        .requestMatchers("/api/admin/results/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "TEACHER", "STUDENT", "DEPARTMENT")

                        // 3. GENERIC ADMIN PATTERN (for other /api/admin/** endpoints)
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "PRINCIPAL", "TEACHER", "DEPARTMENT")

                        // 4. STATE & DISTRICT specific patterns (before generic /api/superadmin/**)
                        .requestMatchers("/api/superadmin/state/**").hasAnyRole("SUPER_ADMIN", "STATE_ADMIN", "DISTRICT_ADMIN")
                        .requestMatchers("/api/superadmin/district/**").hasAnyRole("DISTRICT_ADMIN", "STATE_ADMIN", "SUPER_ADMIN")

                        // 5. SUPER ADMIN APIs - Generic fallback
                        .requestMatchers("/api/superadmin/**").hasAnyRole("SUPER_ADMIN", "DISTRICT_ADMIN", "STATE_ADMIN")

                        // 6. District level access
                        .requestMatchers("/api/district/**").hasAnyRole("DISTRICT_ADMIN", "STATE_ADMIN", "SUPER_ADMIN")

                        // 7. Student APIs
                        .requestMatchers("/api/student/**").hasAnyRole("STUDENT", "ADMIN", "DISTRICT_ADMIN", "STATE_ADMIN", "DEPARTMENT")

                        // 8. All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}