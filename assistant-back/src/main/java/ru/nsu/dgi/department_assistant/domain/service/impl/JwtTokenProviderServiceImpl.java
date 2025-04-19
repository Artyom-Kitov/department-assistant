package ru.nsu.dgi.department_assistant.domain.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.service.SecurityService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProviderServiceImpl {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refreshExpiration}")
    private long jwtRefreshExpirationMs;

    @Value("${jwt.refreshThreshold}")
    private long jwtRefreshThresholdMs;

    private final CookieServiceImpl cookieService;
    private final SecurityService securityService;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(CustomOAuth2User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("roles", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(CustomOAuth2User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            Date now = new Date();
            long timeUntilExpiration = expiration.getTime() - now.getTime();

            return timeUntilExpiration <= jwtRefreshThresholdMs;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return false;
        }
    }

    @Scheduled(fixedRateString = "${jwt.refreshCheckInterval}")
    public void checkAndRefreshTokens() {
        log.debug("Starting scheduled JWT token refresh check");
        refreshTokens();
    }

    public void refreshTokens() {
        try {
            ServletRequestAttributes attributes = getRequestAttributes();

            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User user) {
                processTokenRefresh(request, response, user);
            }
        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage(), e);
        }
    }

    private ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    private void processTokenRefresh(HttpServletRequest request, HttpServletResponse response, CustomOAuth2User user) {
        Optional<String> refreshToken = cookieService.getRefreshTokenFromCookies(request);
        if (refreshToken.isPresent() && validateToken(refreshToken.get())) {
            String email = getEmailFromToken(refreshToken.get());
            CustomOAuth2User authUser = securityService.createCustomOAuth2User(
                securityService.getUserFromDatabase(email),
                email
            );
            refreshAccessTokenIfNeeded(request, response, authUser);
        } else {
            log.warn("Invalid or missing refresh token for user: {}", user.getEmail());
        }
    }

    private void refreshAccessTokenIfNeeded(HttpServletRequest request, HttpServletResponse response, CustomOAuth2User user) {
        Optional<String> accessToken = cookieService.getAccessTokenFromCookies(request);
        if (accessToken.isPresent() && isTokenExpiringSoon(accessToken.get())) {
            String email = user.getEmail();
            log.info("JWT access token for user {} is expiring soon, refreshing...", email);
            String newAccessToken = generateAccessToken(user);
            cookieService.addAccessTokenCookie(response, newAccessToken);
            log.info("Successfully refreshed JWT access token for user: {}", email);
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Long.class);
    }

    public Users.Role getRoleFromToken(String token) {
        return Users.Role.valueOf(Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", String.class));
    }
}