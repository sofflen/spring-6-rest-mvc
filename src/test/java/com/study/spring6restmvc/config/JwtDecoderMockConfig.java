package com.study.spring6restmvc.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.List;

@TestConfiguration
public class JwtDecoderMockConfig {
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    String issuer;

    @Bean("mockJwtDecoder")
    @Primary
    public JwtDecoder jwtDecoder() {
        JwtDecoder jwtDecoder = Mockito.mock(JwtDecoder.class);

        Jwt jwt = Jwt
                .withTokenValue("mock-token")
                .header("alg", "RS256")
                .issuer(issuer)
                .subject("oidc-client")
                .audience(List.of("oidc-client"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("scope", "message.read message.write")
                .build();

        try {
            Mockito.when(jwtDecoder.decode(Mockito.anyString())).thenReturn(jwt);
        } catch (JwtException e) {
            throw new RuntimeException("Test JWT could not be decoded", e);
        }

        return jwtDecoder;
    }
}
