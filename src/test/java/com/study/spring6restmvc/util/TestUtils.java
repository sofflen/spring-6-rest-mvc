package com.study.spring6restmvc.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.Set;

public class TestUtils {
    public static final String AUTH_HEADER_KEY = "Authorization";
    public static final String AUTH_HEADER_MOCK_VALUE = "Bearer Mock";
    public static final String SECRET = "0123456789abcdef0123456789abcdef";// 32 chars for HS256

    public static final String AUTH_HEADER_GENERATED_VALUE = "Bearer "
            + generateJwtToken(Set.of("message.read", "message.write"));

    public static String generateJwtToken(Set<String> scopes) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).build();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer("http://localhost:8081")
                .subject("oidc-client")
                .audience("oidc-client")
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + 3600 * 1000)) // 1 hour expiration
                .claim("scope", String.join(" ", scopes))
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claims);

        try {
            MACSigner signer = new MACSigner(SECRET);
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException("Cannot sign test JWT token", e);
        }

        return signedJWT.serialize();
    }
}
