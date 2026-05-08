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

    @Bean
    public SecretKey jwtSigningKey(JwtProperties jwtProperties) {
        byte[] decoded = Base64.getDecoder().decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(decoded);
    }
}
