package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CookieServiceImpl {
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final String OAUTH2_ACCESS_TOKEN_COOKIE = "oauth2_access_token";
    private static final String OAUTH2_REFRESH_TOKEN_COOKIE = "oauth2_refresh_token";

    @Value("${cookie.domain}")
    private String cookieDomain;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.same-site}")
    private String cookieSameSite;

    @Value("${cookie.http-only}")
    private boolean cookieHttpOnly;

    @Value("${jwt.expiration}")
    private int accessTokenMaxAge;

    @Value("${jwt.refreshExpiration}")
    private int refreshTokenMaxAge;


    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        String cookieHeader = String.format("%s=%s; Path=/; %s; %s; Max-Age=%d; SameSite=%s",
                name, value,
                cookieHttpOnly ? "HttpOnly" : "",
                cookieSecure ? "Secure" : "",
                maxAge,
                cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            cookieHeader += "; Domain=" + cookieDomain;
        }

        response.addHeader("Set-Cookie", cookieHeader);
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    String cookieHeader = String.format("%s=; Path=/; %s; %s; Max-Age=0; SameSite=%s",
                            name,
                            cookieHttpOnly ? "HttpOnly" : "",
                            cookieSecure ? "Secure" : "",
                            cookieSameSite);

                    if (cookieDomain != null && !cookieDomain.isEmpty()) {
                        cookieHeader += "; Domain=" + cookieDomain;
                    }

                    response.addHeader("Set-Cookie", cookieHeader);
                }
            }
        }
    }

    public Optional<String> extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        addCookie(response, ACCESS_TOKEN_COOKIE, token, accessTokenMaxAge);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        addCookie(response, REFRESH_TOKEN_COOKIE, token, refreshTokenMaxAge);
    }

    public void addOAuth2TokensToCookies(HttpServletResponse response, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        if (accessToken != null) {
            addCookie(response, OAUTH2_ACCESS_TOKEN_COOKIE, accessToken.getTokenValue(), accessTokenMaxAge);
        }
        if (refreshToken != null) {
            addCookie(response, OAUTH2_REFRESH_TOKEN_COOKIE, refreshToken.getTokenValue(), refreshTokenMaxAge);
        }
    }

    public Optional<String> getAccessTokenFromCookies(HttpServletRequest request) {
        return extractTokenFromCookies(request, ACCESS_TOKEN_COOKIE);
    }

    public Optional<String> getRefreshTokenFromCookies(HttpServletRequest request) {
        return extractTokenFromCookies(request, REFRESH_TOKEN_COOKIE);
    }

    public Optional<String> getOAuth2AccessTokenFromCookies(HttpServletRequest request) {
        return extractTokenFromCookies(request, OAUTH2_ACCESS_TOKEN_COOKIE);
    }

    public Optional<String> getOAuth2RefreshTokenFromCookies(HttpServletRequest request) {
        return extractTokenFromCookies(request, OAUTH2_REFRESH_TOKEN_COOKIE);
    }

    public void deleteAllAuthCookies(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(request, response, ACCESS_TOKEN_COOKIE);
        deleteCookie(request, response, REFRESH_TOKEN_COOKIE);
        deleteCookie(request, response, OAUTH2_ACCESS_TOKEN_COOKIE);
        deleteCookie(request, response, OAUTH2_REFRESH_TOKEN_COOKIE);
    }
}