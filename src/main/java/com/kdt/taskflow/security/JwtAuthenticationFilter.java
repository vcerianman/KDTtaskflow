package com.kdt.taskflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Flow:
 * 1. Extract the JWT token from the "Authorization: Bearer <token>" HTTP header.
 * 2. Validate the token's signature and expiration time.
 * 3. If valid, build an Authentication object and store it in Spring Security's SecurityContext.
 * 4. Pass the request along to the next filter or controller endpoint.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * Core filter execution method invoked for each HTTP request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Extract raw JWT token string from HTTP header
        String token = extractToken(request);

        // 2. Validate the presence and authenticity/expiration of the JWT token
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            
            // 3. Convert valid JWT token into Spring Security Authentication object (user details + roles)
            UsernamePasswordAuthenticationToken auth = jwtUtils.getAuthentication(token);
            
            if (auth != null) {
                // Set authenticated user into current thread's SecurityContext so Spring Security permits request
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 4. Continue executing the remaining filter chain / Spring MVC controllers
        filterChain.doFilter(request, response);
    }

    /**
     * Helper method to parse the "Authorization" HTTP header.
     * Expected format: "Authorization: Bearer <JWT_TOKEN>"
     * Returns raw JWT token string if present, or null if missing/malformed.
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        
        // Check if header is present and starts with "Bearer " (7 characters)
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7); // Strip "Bearer " prefix and return token string
        }
        return null;
    }
}
