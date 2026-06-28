package com.smartschool.api.service;

import com.smartschool.api.entity.User;
import com.smartschool.api.exception.CustomException;
import com.smartschool.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JavaMailSender mailSender;

    // 🚩 Properties file se sender email uthane ke liye (default fallback value ke saath)
    @Value("${spring.mail.username:noreply@smartschool.com}")
    private String senderEmail;

    // OTP ko RAM mein store karne ke liye (Email -> OTP)
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    // 1. OTP bhejne ka logic
    public void sendOtp(String email) {
        log.info("Processing OTP request for email: {}", email);

        boolean exists = userRepository.findByUsername(email).isPresent();
        if (!exists) {
            // Return 404 to client via GlobalExceptionHandler
            throw new CustomException("User not found with this email", "USER_NOT_FOUND", 404);
        }

        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put(email, otp);

        // 5 minute baad OTP delete karne ka thread (Simple Expiry)
        new Thread(() -> {
            try {
                Thread.sleep(5 * 60 * 1000);
                otpStorage.remove(email);
                log.info("OTP expired and removed for email: {}", email);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // send email and wrap any mail errors
        try {
            sendEmail(email, otp);
        } catch (CustomException ce) {
            // rethrow custom exceptions as-is
            throw ce;
        } catch (Exception e) {
            log.error("Unexpected error while sending OTP: {}", e.getMessage(), e);
            throw new CustomException("Failed to send OTP email", "EMAIL_SEND_FAILED", 502, e);
        }
    }

    // 2. Password update karne ka logic
    public void resetPassword(String email, String otp, String newPassword) {
        String cachedOtp = otpStorage.get(email);

        if (cachedOtp == null || !cachedOtp.equals(otp)) {
            throw new CustomException("Invalid or expired OTP", "INVALID_OTP", 401);
        }

        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException("User not found during reset!", "USER_NOT_FOUND", 404));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpStorage.remove(email); // Kaam hone ke baad cleanup
        log.info("Password successfully reset for user: {}", email);
    }

    private void sendEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            // ✅ YAHI LINE ERROR FIX KAREGI: Sender address batana zaroori hai
            message.setFrom(senderEmail);

            message.setTo(to);
            message.setSubject("Password Reset OTP - Smart School");
            message.setText("Your 6-digit OTP for reset password are: " + otp + "\nYe for 5 minute valid.");

            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            // Wrap and rethrow as CustomException so GlobalExceptionHandler can set a proper status
            throw new CustomException("Email service error: " + e.getMessage(), "EMAIL_SEND_FAILED", 502, e);
        }
    }
}
