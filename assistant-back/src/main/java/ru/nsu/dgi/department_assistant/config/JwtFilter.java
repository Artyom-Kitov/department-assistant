package ru.nsu.dgi.department_assistant.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.service.impl.AuthServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.JwtTokenProviderServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.OAuth2TokenRefreshService;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProviderServiceImpl jwtTokenProviderService;
    private final AuthServiceImpl authService;
    private final OAuth2TokenRefreshService oauthTokenService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        // Проверяем и обновляем JWT токены
        String accessToken = extractTokenFromCookies(request, "accessToken");
        String refreshToken = extractTokenFromCookies(request, "refreshToken");

        if (accessToken != null) {
            if (jwtTokenProviderService.validateToken(accessToken)) {
                // JWT токен валиден, устанавливаем аутентификацию
                setAuthentication(accessToken);
            } else if (refreshToken != null && jwtTokenProviderService.validateToken(refreshToken)) {
                // JWT access token невалиден, но есть валидный refresh token
                try {
                    authService.refreshToken(request, response);
                    log.info("JWT token refreshed successfully");
                    accessToken = extractTokenFromCookies(request, "accessToken");
                    if (accessToken != null) {
                        setAuthentication(accessToken);
                    }
                } catch (Exception e) {
                    log.error("Failed to refresh JWT token: {}", e.getMessage());
                }
            }
        }

        // Проверяем и обновляем OAuth2 токены
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
            String email = user.getEmail();

            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", email);
            if (client != null) {
                OAuth2AccessToken oauth2AccessToken = client.getAccessToken();
                if (oauth2AccessToken != null && oauthTokenService.isTokenExpiringSoon(oauth2AccessToken)) {
                    try {
                        log.info("OAuth2 token is expired, refreshing...");
                        OAuth2AccessToken newToken = oauthTokenService.refreshAccessToken(email);
                        if (newToken != null) {
                            log.info("OAuth2 token refreshed successfully");
                        }
                    } catch (Exception e) {
                        log.error("Failed to refresh OAuth2 token: {}", e.getMessage());
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token) {
        String email = jwtTokenProviderService.getEmailFromToken(token);
        Users.Role role = jwtTokenProviderService.getRoleFromToken(token);
        Long userId = jwtTokenProviderService.getUserIdFromToken(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomOAuth2User user = new CustomOAuth2User(
                    new DefaultOAuth2User(
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.name())),
                            Map.of("email", email), "email"),
                    userId,
                    role
            );

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    private String extractTokenFromCookies(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
