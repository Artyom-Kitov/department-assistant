package ru.nsu.dgi.department_assistant.domain.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.nsu.dgi.department_assistant.domain.exception.TokenRefreshException;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2TokenRefreshService {

    private static final Duration TOKEN_EXPIRY_THRESHOLD = Duration.ofMinutes(5);
    private final CookieServiceImpl cookieService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    private final CustomAuthenticationServiceImpl authenticationService;

    // Используем WebClient с правильной конфигурацией
    private final WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .build();

    /**
     * Обновляет OAuth2 access token используя refresh token.
     *
     * @param email Email пользователя
     * @param httpResponse HTTP response для установки cookies
     * @return Новый OAuth2AccessToken или null в случае ошибки
     */
    public OAuth2AccessToken refreshAccessToken(String email, HttpServletResponse httpResponse) {
        log.debug("Starting token refresh flow for user: {}", email);

        try {
            // 1. Получаем текущего клиента
            OAuth2AuthorizedClient currentClient = authenticationService.getAndValidateAuthorizedClient(email);

            // 2. Получаем и валидируем refresh token
            OAuth2RefreshToken refreshToken = validateRefreshToken(currentClient);

            // 3. Получаем регистрацию клиента
            ClientRegistration registration = authenticationService.getAndValidateClientRegistration();

            // 4. Выполняем запрос на обновление токена
            OAuth2AccessTokenResponse tokenResponse = executeTokenRefreshRequest(registration, refreshToken);

            // 5. Обновляем состояние системы
            return updateSystemState(email, registration, tokenResponse, httpResponse);

        } catch (TokenRefreshException e) {
            log.error("Token refresh failed for user {}: {}", email, e.getMessage());
            return null;
        }
    }

    /**
     * Проверяет, нужно ли обновлять токен.
     */
    public boolean isTokenExpiringSoon(OAuth2AccessToken token) {
        if (token == null || token.getExpiresAt() == null) {
            return true;
        }
        return Instant.now().plus(TOKEN_EXPIRY_THRESHOLD).isAfter(token.getExpiresAt());
    }



    private OAuth2RefreshToken validateRefreshToken(OAuth2AuthorizedClient client) {
        OAuth2RefreshToken refreshToken = client.getRefreshToken();
        if (refreshToken == null) {
            throw new TokenRefreshException("No refresh token available");
        }
        return refreshToken;
    }



    private OAuth2AccessTokenResponse executeTokenRefreshRequest(
            ClientRegistration registration,
            OAuth2RefreshToken refreshToken
    ) {
        log.debug("=== Starting token refresh request ===");
        
        MultiValueMap<String, String> formData = prepareTokenRefreshFormData(registration, refreshToken);
        logRequestDetails(registration, formData);
        
        try {
            return webClient.post()
                    .uri(registration.getProviderDetails().getTokenUri())
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(this::logRawResponse)
                    .map(this::parseAndValidateTokenResponse)
                    .timeout(Duration.ofSeconds(10))
                    .blockOptional()
                    .orElseThrow(() -> new TokenRefreshException("No response from token endpoint"));
        } catch (WebClientResponseException e) {
            handleWebClientError(e);
            throw new TokenRefreshException(
                    String.format("Token refresh request failed: %s - %s",
                            e.getStatusCode(), e.getResponseBodyAsString()));
        }
    }

    private MultiValueMap<String, String> prepareTokenRefreshFormData(
            ClientRegistration registration,
            OAuth2RefreshToken refreshToken
    ) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue());
        formData.add(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue());
        formData.add(OAuth2ParameterNames.CLIENT_ID, registration.getClientId());
        formData.add(OAuth2ParameterNames.CLIENT_SECRET, registration.getClientSecret());
        formData.add(OAuth2ParameterNames.SCOPE, String.join(" ", registration.getScopes()));
        return formData;
    }

    private void logRequestDetails(ClientRegistration registration, MultiValueMap<String, String> formData) {
        log.debug("Preparing request to: {}", registration.getProviderDetails().getTokenUri());
        log.debug("Request parameters: grant_type={}, client_id={}, scopes={}",
                formData.getFirst(OAuth2ParameterNames.GRANT_TYPE),
                formData.getFirst(OAuth2ParameterNames.CLIENT_ID),
                formData.getFirst(OAuth2ParameterNames.SCOPE));
    }

    private void logRawResponse(String rawResponse) {
        log.debug("Received raw response from Google: {}", rawResponse);
    }

    private OAuth2AccessTokenResponse parseAndValidateTokenResponse(String rawResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(rawResponse);
            log.debug("Parsed response structure: {}",
                    node.toString().replaceAll("\"access_token\":\"[^\"]*\"", "\"access_token\":\"[FILTERED]\""));

            validateTokenResponse(node);
            return buildTokenResponse(node);
        } catch (Exception e) {
            log.error("Failed to parse OAuth2 response: {}", e.getMessage());
            throw new TokenRefreshException("Failed to parse OAuth2 response: " + e.getMessage());
        }
    }

    private void validateTokenResponse(JsonNode node) {
        if (node.has("error")) {
            String error = node.get("error").asText();
            String errorDescription = node.has("error_description") ?
                    node.get("error_description").asText() : "No description";
            log.error("OAuth2 error: {} - {}", error, errorDescription);
            throw new TokenRefreshException("OAuth2 error: " + error + " - " + errorDescription);
        }
    }

    private OAuth2AccessTokenResponse buildTokenResponse(JsonNode node) {
        OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse.withToken(
                        node.get("access_token").asText())
                .tokenType(OAuth2AccessToken.TokenType.BEARER);

        if (node.has("expires_in")) {
            builder.expiresIn(node.get("expires_in").asLong());
        }

        if (node.has("refresh_token")) {
            builder.refreshToken(node.get("refresh_token").asText());
        }

        if (node.has("scope")) {
            builder.scopes(new HashSet<>(Arrays.asList(
                    node.get("scope").asText().split(" "))));
        }

        OAuth2AccessTokenResponse response = builder.build();
        logTokenResponseDetails(response);
        return response;
    }

    private void logTokenResponseDetails(OAuth2AccessTokenResponse response) {
        assert response.getAccessToken() != null;
        log.debug("Successfully built OAuth2AccessTokenResponse. Access token present: {}, " +
                        "Refresh token present: {}, Expires in: {} seconds",
                response.getAccessToken(),
                response.getRefreshToken() != null,
                response.getAccessToken().getExpiresAt() != null ?
                        Duration.between(Instant.now(),
                                response.getAccessToken().getExpiresAt()).getSeconds() : null);
    }

    private void handleWebClientError(WebClientResponseException e) {
        log.error("Token refresh request failed. Status: {}, Body: {}",
                e.getStatusCode(), e.getResponseBodyAsString());
    }

    private OAuth2AccessToken updateSystemState(
            String email,
            ClientRegistration registration,
            OAuth2AccessTokenResponse tokenResponse,
            HttpServletResponse httpResponse
    ) {
        validateTokenResponse(tokenResponse);
        OAuth2AccessToken newAccessToken = tokenResponse.getAccessToken();
        OAuth2RefreshToken newRefreshToken = tokenResponse.getRefreshToken();
        
        log.debug("New access token obtained. Token value length: {}, Expires at: {}",
                newAccessToken.getTokenValue().length(),
                newAccessToken.getExpiresAt());
        log.debug("New refresh token present: {}", newRefreshToken != null);

        OAuth2RefreshToken finalRefreshToken = determineFinalRefreshToken(email, newRefreshToken);
        OAuth2AuthorizedClient newClient = authenticationService.createNewAuthorizedClient(registration, email, newAccessToken, finalRefreshToken);
        
        authenticationService.saveAuthorizedClient(email, newClient);
        cookieService.addOAuth2TokensToCookies(httpResponse, newAccessToken, newRefreshToken);
        
        return newAccessToken;
    }

    private void validateTokenResponse(OAuth2AccessTokenResponse tokenResponse) {
        if (tokenResponse == null) {
            log.error("Cannot update system state with null token response");
            throw new TokenRefreshException("Token response is null");
        }

        if (tokenResponse.getAccessToken() == null) {
            log.error("Token response contains null access token");
            throw new TokenRefreshException("New access token is null");
        }
    }

    public void processAuthentication(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> accessToken = cookieService.getOAuth2AccessTokenFromCookies(request);

        if (accessToken.isPresent()) {
            processAccessToken(accessToken.get(), request, response);
        } else {
            processRefreshToken(request, response);
        }
    }

    private OAuth2RefreshToken determineFinalRefreshToken(String email, OAuth2RefreshToken newRefreshToken) {
        return newRefreshToken != null ?
                newRefreshToken :
                authenticationService.getAndValidateAuthorizedClient(email).getRefreshToken();
    }



    private void processRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshToken = cookieService.getOAuth2RefreshTokenFromCookies(request);
        refreshToken.ifPresent(s -> processRefreshTokenValue(s, response));
    }

    private void processAccessToken(String accessToken, HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizedClient client = findAuthorizedClientByToken(accessToken);

        if (client != null) {
            String email = client.getPrincipalName();
            log.debug("Found client for email: {}", email);

            if (isTokenExpiringSoon(client.getAccessToken())) {
                refreshExpiringToken(email, response);
            } else {
                authenticationService.updateSecurityContext(email);
            }
        }
    }

    private void refreshExpiringToken(String email, HttpServletResponse response) {
        log.debug("Access token is expiring soon, attempting refresh");
        OAuth2AccessToken newToken = refreshAccessToken(email, response);
        if (newToken != null) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", email);
            authenticationService.updateSecurityContext(email);
        }
    }

    private void processRefreshTokenValue(String refreshToken, HttpServletResponse response) {
        try {
            OAuth2AuthorizedClient client = findClientByRefreshToken(refreshToken);

            if (client != null) {
                String email = client.getPrincipalName();
                log.debug("Found client by refresh token for email: {}", email);

                OAuth2AccessToken newToken = refreshAccessToken(email, response);
                if (newToken != null) {
                    client = authorizedClientService.loadAuthorizedClient("google", email);
                    authenticationService.updateSecurityContext(email);
                    log.info("Successfully refreshed access token using refresh token");
                }
            }
        } catch (Exception e) {
            log.error("Failed to refresh token using refresh token", e);
        }
    }

    private OAuth2AuthorizedClient findClientByRefreshToken(String refreshToken) {
        try {
            Field field = authorizedClientService.getClass().getDeclaredField("authorizedClients");
            field.setAccessible(true);
            Map<?, ?> clients = (Map<?, ?>) field.get(authorizedClientService);

            for (Object client : clients.values()) {
                if (client instanceof OAuth2AuthorizedClient oauth2Client) {
                    OAuth2RefreshToken clientRefreshToken = oauth2Client.getRefreshToken();
                    if (clientRefreshToken != null &&
                            clientRefreshToken.getTokenValue().equals(refreshToken)) {
                        return oauth2Client;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error accessing authorized clients", e);
        }
        return null;
    }















    private OAuth2AuthorizedClient findAuthorizedClientByToken(String accessToken) {
        try {
            Field field = authorizedClientService.getClass().getDeclaredField("authorizedClients");
            field.setAccessible(true);
            Map<?, ?> clients = (Map<?, ?>) field.get(authorizedClientService);

            for (Object client : clients.values()) {
                if (client instanceof OAuth2AuthorizedClient oauth2Client) {
                    if (oauth2Client.getAccessToken().getTokenValue().equals(accessToken)) {
                        return oauth2Client;
                    }
                }
            }
            log.warn("No client found for token: {}", accessToken.substring(0, 10) + "...");
        } catch (Exception e) {
            log.error("Error accessing authorized clients", e);
        }
        return null;
    }
}


