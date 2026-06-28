package com.smartschool.api.service;

import com.smartschool.api.dto.LoginRequest;
import com.smartschool.api.dto.AuthResponse;
import java.util.Map;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    // 🚩 NAYA: Department login ke liye
    AuthResponse departmentLogin(Map<String, String> loginData);
}