package com.smartschool.api.dto;

import lombok.Data;

@Data
public class LoginRequest {
    // Principal/Teacher/Student ka Email hi username hoga
    private String username;
    private String password;
}