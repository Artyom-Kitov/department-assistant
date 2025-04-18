package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final JwtTokenProviderServiceImpl tokenProvider;
    private final UserRepository userRepository;
    private final CookieServiceImpl cookieService;
    private final OAuth2TokenRefreshService oauth2Service;

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.extractTokenFromCookies(request, "refreshToken")
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
