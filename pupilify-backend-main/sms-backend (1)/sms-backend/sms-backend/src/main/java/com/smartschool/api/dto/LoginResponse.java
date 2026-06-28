package com.smartschool.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String email;
    private String role;
    private String name;
    private Boolean active;
    private String message;
    private Boolean success;
    private String token;
}
