package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2TokenRefreshService {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CookieServiceImpl cookieService;
    private final WebClient webClient = WebClient.builder().build();

    @Scheduled(fixedRate = 300000) // Каждые 5 минут
    public void checkAndRefreshTokens() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
            String email = user.getEmail();

            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", email);
            if (client != null) {
                OAuth2AccessToken accessToken = client.getAccessToken();
                if (isTokenExpiringSoon(accessToken)) {
                    log.info("Token for user {} is expiring soon, refreshing...", email);
                    refreshAccessToken(email);
                }
            }
        }
    }

    public OAuth2AccessToken refreshAccessToken(String email) {
        log.info("Refreshing OAuth access token for user: {}", email);

        // 1. Получаем текущего клиента
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", email);
        if (client == null) {
            log.warn("No authorized client found for user: {}", email);
            return null;
        }

        // 2. Проверяем наличие refresh token
        OAuth2RefreshToken refreshToken = client.getRefreshToken();
        if (refreshToken == null) {
            log.warn("No refresh token available for user: {}", email);
            return null;
        }

        // 3. Получаем конфигурацию клиента
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("google");
        if (registration == null) {
            log.error("Google client registration not found");
            return null;
        }

        // 4. Формируем запрос на обновление токена
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2ParameterNames.GRANT_TYPE, "refresh_token");
        formData.add(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue());
        formData.add("client_id", registration.getClientId());
        formData.add("client_secret", registration.getClientSecret());

        // 5. Отправляем запрос на обновление токена
        OAuth2AccessTokenResponse response = webClient.post()
                .uri(registration.getProviderDetails().getTokenUri())
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OAuth2AccessTokenResponse.class)
                .block();

        if (response == null) {
            log.error("Failed to refresh access token for user: {}", email);
            return null;
        }

        // 6. Создаем новый access token
        OAuth2AccessToken newAccessToken = response.getAccessToken();
        log.info("Successfully refreshed access token for user: {}", email);

        // 7. Обновляем токены в сервисах
        OAuth2AuthorizedClient newClient = new OAuth2AuthorizedClient(
                registration,
                email,
                newAccessToken,
                refreshToken
        );

        authorizedClientService.saveAuthorizedClient(newClient, null);

        return newAccessToken;
    }

    public boolean isTokenExpiringSoon(OAuth2AccessToken token) {
        if (token == null || token.getExpiresAt() == null) {
            return false;
        }
        Instant now = Instant.now();
        Instant expiresAt = token.getExpiresAt();
        return expiresAt.minusSeconds(600).isBefore(now); // 10 минут до истечения
    }

    public void updateTokensInCookies(HttpServletResponse response, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        if (accessToken != null) {
            cookieService.addCookie(response, "oauth2_access_token", accessToken.getTokenValue(), 60 * 60); // 1 час
        }
        if (refreshToken != null) {
            cookieService.addCookie(response, "oauth2_refresh_token", refreshToken.getTokenValue(), 60 * 60 * 24 * 7); // 7 дней
        }
    }

    public Optional<OAuth2AccessToken> getAccessTokenFromCookies(HttpServletRequest request) {
        return cookieService.extractTokenFromCookies(request, "oauth2_access_token")
                .map(tokenValue -> new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        tokenValue,
                        Instant.now(),
                        Instant.now().plusSeconds(3600)));
    }

    public Optional<OAuth2RefreshToken> getRefreshTokenFromCookies(HttpServletRequest request) {
        return cookieService.extractTokenFromCookies(request, "oauth2_refresh_token")
                .map(tokenValue -> new OAuth2RefreshToken(tokenValue, Instant.now()));
    }
} 