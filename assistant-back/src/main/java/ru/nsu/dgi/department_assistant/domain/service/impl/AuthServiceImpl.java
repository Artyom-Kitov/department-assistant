package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final CookieServiceImpl cookieService;


    public void logout(HttpServletResponse response) {
        cookieService.deleteOAuth2Cookies(response);
        SecurityContextHolder.clearContext();
    }

    public Map<String, Object> getAuthStatus(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User user) {
            response.put("authenticated", true);
            response.put("email", user.getEmail());
            response.put("name", user.getDisplayName());
            response.put("role", user.getRole().name());

            // Добавляем информацию о токенах
            Optional<String> oauth2AccessToken = cookieService.getOAuth2AccessTokenFromCookies(request);
            Optional<String> oauth2RefreshToken = cookieService.getOAuth2RefreshTokenFromCookies(request);

            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("hasAccessToken", oauth2AccessToken.isPresent());
            tokenInfo.put("hasRefreshToken", oauth2RefreshToken.isPresent());

            response.put("tokenInfo", tokenInfo);
        } else {
            response.put("authenticated", false);
        }

        return response;
    }

    // Если нужна проверка доступа для конкретных email доменов
    public boolean isAllowedDomain(String email) {
        return email != null && email.endsWith("@nsu.ru");
    }
}
