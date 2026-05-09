package com.example.bankcards.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    private static final String DEFAULT_JWT_SECRET_BASE64 = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Bean
    public SecretKey jwtSigningKey(JwtProperties jwtProperties) {
        String configuredSecret = jwtProperties.getSecret();
        String secretToUse = configuredSecret;
        if (configuredSecret == null || configuredSecret.isBlank() || configuredSecret.startsWith("${")) {
            secretToUse = DEFAULT_JWT_SECRET_BASE64;
        }

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(secretToUse);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("JWT secret must be a valid Base64 string", ex);
        }
        return Keys.hmacShaKeyFor(decoded);
    }
}
