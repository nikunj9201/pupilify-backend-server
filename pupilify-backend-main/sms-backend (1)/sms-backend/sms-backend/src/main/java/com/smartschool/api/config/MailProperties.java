package com.smartschool.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Mail Properties Class
 * Reads mail configuration from application.properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String defaultEncoding;

}

