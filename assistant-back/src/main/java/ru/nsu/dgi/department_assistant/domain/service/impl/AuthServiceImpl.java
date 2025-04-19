package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final JwtTokenProviderServiceImpl tokenProvider;
    private final CookieServiceImpl cookieService;
    private final OAuth2TokenRefreshService oauth2Service;

    public void refreshToken(HttpServletRequest request) {
        String refreshToken = cookieService.extractTokenFromCookies(request, "refreshToken")
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        tokenProvider.checkAndRefreshTokens();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.deleteCookie(request, response, "accessToken");
        cookieService.deleteCookie(request, response, "refreshToken");
        SecurityContextHolder.clearContext();
    }

    public void restoreOAuth2Authorization(HttpServletRequest request) {
        oauth2Service.restoreOAuth2Authorization(request);
    }

    public Map<String, Object> getOAuth2Status(HttpServletRequest request) {
        return oauth2Service.getOAuth2Status(request);
    }
}
