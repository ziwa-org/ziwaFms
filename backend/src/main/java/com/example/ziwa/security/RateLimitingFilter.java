package com.example.ziwa.security;

import com.example.ziwa.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filter that implements rate limiting for authentication endpoints.
 * Limits requests to 5 per minute per IP address.
 */
@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int RATE_LIMIT = 5; // requests per minute
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Only apply rate limiting to authentication endpoints
        if (path.startsWith("/api/auth/")) {
            String clientIp = getClientIp(request);
            Bucket bucket = resolveBucket(clientIp);
            
            if (bucket.tryConsume(1)) {
                // Request allowed
                filterChain.doFilter(request, response);
            } else {
                // Rate limit exceeded
                log.warn("Rate limit exceeded for IP: {}", clientIp);
                sendRateLimitError(response);
            }
        } else {
            // Not an auth endpoint, proceed without rate limiting
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Resolves or creates a bucket for the given client IP.
     */
    private Bucket resolveBucket(String clientIp) {
        return buckets.computeIfAbsent(clientIp, key -> createNewBucket());
    }

    /**
     * Creates a new bucket with the configured rate limit.
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(RATE_LIMIT, Refill.intervally(RATE_LIMIT, REFILL_DURATION));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Extracts the client IP address from the request.
     * Checks X-Forwarded-For header first (for proxied requests).
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Sends a 429 Too Many Requests error response.
     */
    private void sendRateLimitError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "RATE_LIMIT_EXCEEDED",
            "Too many requests. Please try again later.",
            LocalDateTime.now()
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
