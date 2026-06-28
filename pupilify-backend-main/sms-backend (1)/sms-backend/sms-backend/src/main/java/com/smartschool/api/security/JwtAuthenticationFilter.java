package com.smartschool.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.getRoleFromToken(token);
                
                System.out.println("🔐 JWT Filter - Username: " + username + ", Role from token: " + role);
                
                // ✅ IMPORTANT: Verify user still exists in database
                try {
                    userDetailsService.loadUserByUsername(username);
                    System.out.println("✅ User verified in database: " + username);
                } catch (UsernameNotFoundException e) {
                    System.out.println("❌ User not found in database (possible database reset): " + username);
                    System.out.println("❌ Clearing security context - user must log in again");
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                // ✅ Ensure role has ROLE_ prefix for Spring Security
                String authority = role;
                if (role != null && !role.startsWith("ROLE_")) {
                    authority = "ROLE_" + role;
                }
                
                System.out.println("🔐 Setting authority: " + authority);
                
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Auto-Renewal Logic
                if (jwtUtil.isTokenNearExpiration(token)) {
                    String newToken = jwtUtil.generateToken(username, role);
                    response.setHeader("New-Token", newToken);
                    response.setHeader("Access-Control-Expose-Headers", "New-Token");
                    System.out.println("🔄 Token renewed for: " + username);
                }
            } else {
                System.out.println("❌ Invalid token for request: " + request.getRequestURI());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}