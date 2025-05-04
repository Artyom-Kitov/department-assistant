package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CookieServiceImpl {
    // Константы для имен куки
    private static final String OAUTH2_ACCESS_TOKEN_COOKIE = "oauth2_access_token";
    private static final String OAUTH2_REFRESH_TOKEN_COOKIE = "oauth2_refresh_token";

    // Константы для времени жизни куки
    private static final int OAUTH2_ACCESS_TOKEN_MAX_AGE = 3600;  // 1 hour
    private static final int OAUTH2_REFRESH_TOKEN_MAX_AGE = 7200; // 2 hours

    /**
     * Добавляет OAuth2 токены в куки
     */
    public void addOAuth2TokensToCookies(HttpServletResponse response,
                                         OAuth2AccessToken accessToken,
                                         OAuth2RefreshToken refreshToken) {
        log.debug("Adding OAuth2 tokens to cookies");

        validateAccessToken(accessToken);

        // Access Token
        Cookie accessTokenCookie = createSecureCookie(
                OAUTH2_ACCESS_TOKEN_COOKIE,
                accessToken.getTokenValue(),
                OAUTH2_ACCESS_TOKEN_MAX_AGE
        );
        response.addCookie(accessTokenCookie);
        log.debug("Added OAuth2 access token cookie");

        // Refresh Token
        if (refreshToken != null && refreshToken.getTokenValue() != null) {
            Cookie refreshTokenCookie = createSecureCookie(
                    OAUTH2_REFRESH_TOKEN_COOKIE,
                    refreshToken.getTokenValue(),
                    OAUTH2_REFRESH_TOKEN_MAX_AGE
            );
            response.addCookie(refreshTokenCookie);
            log.debug("Added OAuth2 refresh token cookie");
        }
    }

    /**
     * Получает OAuth2 access token из куки
     */
    public Optional<String> getOAuth2AccessTokenFromCookies(HttpServletRequest request) {
        return extractTokenFromCookies(request, OAUTH2_ACCESS_TOKEN_COOKIE);
    }

    /**
     * Получает OAuth2 refresh token из куки
     */
    public Optional<String> getOAuth2RefreshTokenFromCookies(HttpServletRequest request) {
        return extractTokenFromCookies(request, OAUTH2_REFRESH_TOKEN_COOKIE);
    }

    private Cookie createSecureCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    private Optional<String> extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    private void validateAccessToken(OAuth2AccessToken accessToken) {
        if (accessToken == null) {
            log.error("Attempt to add null access token to cookies");
            throw new IllegalArgumentException("accessToken cannot be null");
        }

        if (accessToken.getTokenValue() == null) {
            log.error("Access token has null token value");
            throw new IllegalArgumentException("access token value cannot be null");
        }
    }

    /**
     * Удаляет все OAuth2 куки
     */
    public void deleteOAuth2Cookies(HttpServletResponse response) {
        deleteCookie(response, OAUTH2_ACCESS_TOKEN_COOKIE);
        deleteCookie(response, OAUTH2_REFRESH_TOKEN_COOKIE);
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}