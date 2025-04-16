package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenRefreshScheduler {

    private final JwtTokenProviderServiceImpl tokenProvider;
    private final CookieServiceImpl cookieService;
    private final AuthServiceImpl authService;

    @Scheduled(fixedRateString = "${jwt.refreshCheckInterval:300000}") // default 5 minutes
    public void checkAndRefreshTokens() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                log.debug("No active request context, skipping token refresh check");
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();
            if (response == null) {
                log.debug("No active response context, skipping token refresh check");
                return;
            }

            Optional<String> accessToken = cookieService.extractTokenFromCookies(request, "accessToken");
            if (accessToken.isPresent() && tokenProvider.isTokenExpiringSoon(accessToken.get())) {
                log.info("Access token is expiring soon, refreshing...");
                authService.refreshToken(request, response);
            }
        } catch (Exception e) {
            log.error("Error during token refresh check: {}", e.getMessage());
        }
    }
} 