package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//@Service
//@RequiredArgsConstructor
//public class AuthServiceImpl {
//
//    private final JwtTokenProviderServiceImpl tokenProvider;
//    private final CookieServiceImpl cookieService;
//    private final OAuth2TokenRefreshService oauth2Service;
//
//    public void refreshToken(HttpServletRequest request) {
//        String refreshToken = cookieService.extractTokenFromCookies(request, "refreshToken")
//                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
//
//        if (!tokenProvider.validateToken(refreshToken)) {
//            throw new RuntimeException("Invalid refresh token");
//        }
//
//        tokenProvider.checkAndRefreshTokens();
//    }
//
//    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        cookieService.deleteCookie(request, response, "accessToken");
//        cookieService.deleteCookie(request, response, "refreshToken");
//        SecurityContextHolder.clearContext();
//    }
//
//    public void restoreOAuth2Authorization(HttpServletRequest request) {
//        oauth2Service.restoreOAuth2Authorization(request);
//    }
//
//    public Map<String, Object> getOAuth2Status(HttpServletRequest request) {
//        return oauth2Service.getOAuth2Status(request);
//    }
//}
@Service
@RequiredArgsConstructor
public class AuthServiceImpl {
    private final OAuth2TokenRefreshService oauth2Service;
    private final CookieServiceImpl cookieService;
    private final UserRepository userRepository;

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.deleteOAuth2Cookies(request,response);
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
