// ResetPasswordRequest.java
package com.smartschool.api.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}