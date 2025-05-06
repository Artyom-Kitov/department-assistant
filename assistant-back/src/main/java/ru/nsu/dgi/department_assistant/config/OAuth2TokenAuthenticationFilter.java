package ru.nsu.dgi.department_assistant.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.nsu.dgi.department_assistant.domain.service.impl.OAuth2TokenRefreshService;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2TokenAuthenticationFilter extends OncePerRequestFilter {
    private final OAuth2TokenRefreshService tokenService;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return isPublicEndpoint(path);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/oauth2") ||
                path.startsWith("/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("=== Starting OAuth2 Filter for path: {} ===", request.getRequestURI());

        try {
            tokenService.processAuthentication(request, response);
        } catch (Exception e) {
            log.error("Authentication error", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}