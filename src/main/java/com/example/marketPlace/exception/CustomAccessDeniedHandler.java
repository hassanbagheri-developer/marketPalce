package com.example.marketPlace.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", 403,
                "error", "FORBIDDEN",
                "message", "You do not have permission to access this resource",
                "path", request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
