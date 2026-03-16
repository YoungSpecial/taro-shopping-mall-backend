package com.mall.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter implements Filter {

    private static final int RATE_LIMIT = 100;
    private static final int RATE_LIMIT_PERIOD_MINUTES = 1;

    private final ConcurrentHashMap<String, RequestRecord> requestRecords = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIp(httpRequest);

        if (!checkRateLimit(clientIp, httpResponse)) {
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean checkRateLimit(String clientIp, HttpServletResponse response) throws IOException {
        RequestRecord record = requestRecords.computeIfAbsent(clientIp, k -> new RequestRecord());

        LocalDateTime now = LocalDateTime.now();

        if (record.getTimestamp().plusMinutes(RATE_LIMIT_PERIOD_MINUTES).isBefore(now)) {
            record.setTimestamp(now);
            record.getCounter().set(0);
        }

        if (record.getCounter().incrementAndGet() > RATE_LIMIT) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded\"}");
            return false;
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private static class RequestRecord {
        private LocalDateTime timestamp = LocalDateTime.now();
        private final AtomicInteger counter = new AtomicInteger(0);

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public AtomicInteger getCounter() {
            return counter;
        }
    }
}
