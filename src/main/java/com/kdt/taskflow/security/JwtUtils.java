package com.kdt.taskflow.security;

import com.kdt.taskflow.mapper.UserMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Utility component responsible for handling JWT operations:
 * - Secret key initialization from application.yml config
 * - Token generation for authenticated users
 * - Token parsing and username extraction
 * - Token signature & expiration validation
 * - Building Spring Security UsernamePasswordAuthenticationToken for SecurityContext
 */
@Component
public class JwtUtils {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final UserMapper userMapper;

    /**
     * Constructor injecting configuration properties from application.yml.
     * Decodes Base64 secret string into an HMAC SHA SecretKey.
     */
    public JwtUtils(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs,
            UserMapper userMapper) {
        // Convert Base64 encoded secret string into HMAC SHA SecretKey object
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
        this.userMapper = userMapper;
    }

    /**
     * Generates a signed JWT access token for a given username.
     * Sets subject (username), issuance timestamp, expiration timestamp, and HMAC-SHA signature.
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)                 // Set subject payload to username
                .issuedAt(now)                      // Set token creation time
                .expiration(expiryDate)            // Set token expiration time (e.g., 24 hours)
                .signWith(secretKey)               // Sign with HMAC SHA secret key
                .compact();                        // Serialize to URL-safe JWT string
    }

    /**
     * Extracts the subject (username) from a valid JWT token payload.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validates the JWT token signature and expiration.
     * Returns true if token is valid and unexpired; false if invalid, corrupted, or expired.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token); // Throws exception if signature mismatch or token expired
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Catches ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, etc.
            return false;
        }
    }

    /**
     * Converts a valid JWT token into a Spring Security UsernamePasswordAuthenticationToken.
     * Looks up user details and assigns granted authorities (e.g. ROLE_ADMIN, ROLE_MEMBER).
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        
        // Lookup user in DB to build granted authorities based on user's assigned role
        return userMapper.findByUsername(username)
                .map(user -> new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                ))
                .orElse(null);
    }

    /**
     * Helper method to parse and verify JWT claims payload using the SecretKey.
     * Throws JwtException if verification fails.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)            // Verify signature using the HMAC secret key
                .build()
                .parseSignedClaims(token)         // Parse signed JWT claims payload
                .getPayload();
    }
}
