package com.banking_portal.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);
    private static final String SECRET_KEY = "aVerySecureBase64EncodedKeyWithAtLeast32Characters";

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60*60*24;


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String uid, String phone_number) {
        return Jwts.builder()
                .setSubject(uid)// Subject is userId
                .claim("uid",uid)
                .claim("phone_number",phone_number)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUserId(String token) {
        return getClaimFromToken(token, claims -> claims.get("uid", String.class));
    }

    public String extractPhoneNumber(String token) {
        return getClaimFromToken(token, claims -> claims.get("phone_number", String.class));
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Use the new method with Key
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.error("Failed to extract claims from token: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateAccessToken(String token, String phone_number, String uid) {
        // Check both claims and expiration
        return phone_number.equals(extractPhoneNumber(token))
                && uid.equals(extractUserId(token))
                && !isTokenExpired(token);
    }


    private boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
