package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenStorageServiceImpl {
    private final ConcurrentHashMap<String, OAuth2Tokens> tokenStorage = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 3600000) // Очистка каждый час
    public void cleanupExpiredTokens() {
        tokenStorage.entrySet().removeIf(entry ->
                entry.getValue().accessToken().getExpiresAt().isBefore(Instant.now())
        );
    }

    public void storeGoogleTokens(String email, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        tokenStorage.put(email, new OAuth2Tokens(accessToken, refreshToken));
    }

    public Optional<OAuth2Tokens> getGoogleTokens(String email) {
        return Optional.ofNullable(tokenStorage.get(email));
    }

    public record OAuth2Tokens(OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {}
}