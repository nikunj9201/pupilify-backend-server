package com.smartschool.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. General uploads handler
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(0);

        // 2. Student documents handler
        registry.addResourceHandler("/api/admin/students/files/**")
                .addResourceLocations("file:" + uploadDir + "/students/")
                .setCachePeriod(0);

        // 3. 🚩 NAYA: School Logos handler
        // Jab browser '/uploads/logos/**' maange, toh system ke folder mein dekho
        registry.addResourceHandler("/uploads/logos/**")
                .addResourceLocations("file:" + uploadDir + "/schools/")
                .setCachePeriod(0);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 🚩 CORS Fix: Ye frontend (3000) ko backend (8080) se data/file lene ki permission deta hai
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type", "Content-Disposition")
                .allowCredentials(false);
    }
}