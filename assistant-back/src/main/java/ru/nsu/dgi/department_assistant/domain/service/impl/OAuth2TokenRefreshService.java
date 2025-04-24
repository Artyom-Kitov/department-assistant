package ru.nsu.dgi.department_assistant.domain.service.impl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.exception.TokenRefreshException;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2TokenRefreshService {
    private static final String GOOGLE_REGISTRATION_ID = "google";
    private static final Duration TOKEN_EXPIRY_THRESHOLD = Duration.ofMinutes(5);

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CookieServiceImpl cookieService;

    private final UserRepository userRepository;

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
            OAuth2AuthorizedClient currentClient = getAndValidateAuthorizedClient(email);

            // 2. Получаем и валидируем refresh token
            OAuth2RefreshToken refreshToken = validateRefreshToken(currentClient);

            // 3. Получаем регистрацию клиента
            ClientRegistration registration = getAndValidateClientRegistration();

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

    private OAuth2AuthorizedClient getAndValidateAuthorizedClient(String email) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(GOOGLE_REGISTRATION_ID, email);
        if (client == null) {
            throw new TokenRefreshException("No authorized client found");
        }
        return client;
    }

    private OAuth2RefreshToken validateRefreshToken(OAuth2AuthorizedClient client) {
        OAuth2RefreshToken refreshToken = client.getRefreshToken();
        if (refreshToken == null) {
            throw new TokenRefreshException("No refresh token available");
        }
        return refreshToken;
    }

    private ClientRegistration getAndValidateClientRegistration() {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(GOOGLE_REGISTRATION_ID);
        if (registration == null) {
            throw new TokenRefreshException("Google client registration not found");
        }
        return registration;
    }



    private OAuth2AccessTokenResponse executeTokenRefreshRequest(
            ClientRegistration registration,
            OAuth2RefreshToken refreshToken
    ) {
        log.debug("=== Starting token refresh request ===");

        // Подготовка данных запроса
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue());
        formData.add(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue());
        formData.add(OAuth2ParameterNames.CLIENT_ID, registration.getClientId());
        formData.add(OAuth2ParameterNames.CLIENT_SECRET, registration.getClientSecret());
        formData.add(OAuth2ParameterNames.SCOPE, String.join(" ", registration.getScopes()));

        log.debug("Preparing request to: {}", registration.getProviderDetails().getTokenUri());
        log.debug("Request parameters: grant_type={}, client_id={}, scopes={}",
                AuthorizationGrantType.REFRESH_TOKEN.getValue(),
                registration.getClientId(),
                registration.getScopes());

        try {
            return webClient.post()
                    .uri(registration.getProviderDetails().getTokenUri())
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class) // Сначала получаем сырой ответ
                    .doOnNext(rawResponse -> {
                        log.debug("Received raw response from Google: {}", rawResponse);
                    })
                    .map(rawResponse -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode node = mapper.readTree(rawResponse);
                            log.debug("Parsed response structure: {}",
                                    node.toString().replaceAll("\"access_token\":\"[^\"]*\"", "\"access_token\":\"[FILTERED]\""));

                            // Проверяем наличие ошибок
                            if (node.has("error")) {
                                String error = node.get("error").asText();
                                String errorDescription = node.has("error_description") ?
                                        node.get("error_description").asText() : "No description";
                                log.error("OAuth2 error: {} - {}", error, errorDescription);
                                throw new TokenRefreshException("OAuth2 error: " + error + " - " + errorDescription);
                            }

                            // Создаем билдер для ответа
                            OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse.withToken(
                                            node.get("access_token").asText())
                                    .tokenType(OAuth2AccessToken.TokenType.BEARER);

                            // Добавляем expires_in если есть
                            if (node.has("expires_in")) {
                                builder.expiresIn(node.get("expires_in").asLong());
                            }

                            // Добавляем refresh_token если есть
                            if (node.has("refresh_token")) {
                                builder.refreshToken(node.get("refresh_token").asText());
                            }

                            // Добавляем scope если есть
                            if (node.has("scope")) {
                                builder.scopes(new HashSet<>(Arrays.asList(
                                        node.get("scope").asText().split(" "))));
                            }

                            OAuth2AccessTokenResponse response = builder.build();
                            log.debug("Successfully built OAuth2AccessTokenResponse. Access token present: {}, " +
                                            "Refresh token present: {}, Expires in: {} seconds",
                                    response.getAccessToken() != null,
                                    response.getRefreshToken() != null,
                                    response.getAccessToken().getExpiresAt() != null ?
                                            Duration.between(Instant.now(),
                                                    response.getAccessToken().getExpiresAt()).getSeconds() : null);

                            return response;
                        } catch (Exception e) {
                            log.error("Failed to parse OAuth2 response: {}", e.getMessage());
                            throw new TokenRefreshException("Failed to parse OAuth2 response: " + e.getMessage());
                        }
                    })
                    .timeout(Duration.ofSeconds(10))
                    .blockOptional()
                    .orElseThrow(() -> new TokenRefreshException("No response from token endpoint"));
        } catch (WebClientResponseException e) {
            log.error("Token refresh request failed. Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new TokenRefreshException(
                    String.format("Token refresh request failed: %s - %s",
                            e.getStatusCode(), e.getResponseBodyAsString()));
        }
    }


        private OAuth2AccessToken updateSystemState(
                String email,
                ClientRegistration registration,
                OAuth2AccessTokenResponse tokenResponse,
                HttpServletResponse httpResponse
        ) {
            if (tokenResponse == null) {
                log.error("Cannot update system state with null token response");
                throw new TokenRefreshException("Token response is null");
            }

            OAuth2AccessToken newAccessToken = tokenResponse.getAccessToken();
            if (newAccessToken == null) {
                log.error("Token response contains null access token");
                throw new TokenRefreshException("New access token is null");
            }

            log.debug("New access token obtained. Token value length: {}, Expires at: {}",
                    newAccessToken.getTokenValue().length(),
                    newAccessToken.getExpiresAt());

            OAuth2RefreshToken newRefreshToken = tokenResponse.getRefreshToken();
            log.debug("New refresh token present: {}", newRefreshToken != null);

            // Используем новый refresh token если он есть, иначе сохраняем старый
            OAuth2RefreshToken finalRefreshToken = newRefreshToken != null ?
                    newRefreshToken :
                    getAndValidateAuthorizedClient(email).getRefreshToken();

            // Создаем нового клиента
            OAuth2AuthorizedClient newClient = new OAuth2AuthorizedClient(
                    registration,
                    email,
                    newAccessToken,
                    finalRefreshToken
            );

            // Создаем Authentication объект для сохранения клиента
            try {
                Authentication authentication = createAuthenticationForClient(email);
                authorizedClientService.saveAuthorizedClient(newClient, authentication);
                log.debug("Successfully saved new authorized client");
            } catch (Exception e) {
                log.error("Failed to save authorized client", e);
                throw new TokenRefreshException("Failed to save authorized client: " + e.getMessage());
            }

            // Обновляем куки
            try {
                cookieService.addOAuth2TokensToCookies(httpResponse, newAccessToken, finalRefreshToken);
                log.debug("Successfully updated OAuth2 cookies");
            } catch (Exception e) {
                log.error("Failed to update cookies", e);
                throw new TokenRefreshException("Failed to update cookies: " + e.getMessage());
            }

            return newAccessToken;
        }

        /**
         * Создает объект Authentication для сохранения OAuth2AuthorizedClient
         */
        private Authentication createAuthenticationForClient(String email) {
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new TokenRefreshException("User not found: " + email));

            Collection<GrantedAuthority> authorities = Collections.singleton(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );

            // Создаем OAuth2User
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("email", email);
            attributes.put("sub", email);

            OAuth2User oauth2User = new DefaultOAuth2User(
                    authorities,
                    attributes,
                    "email"
            );

            return new OAuth2AuthenticationToken(
                    oauth2User,
                    authorities,
                    "google"  // registrationId
            );
        }


        public void updateTokensInCookies(HttpServletResponse response,
                                      OAuth2AccessToken accessToken,
                                      OAuth2RefreshToken refreshToken) {
        cookieService.addOAuth2TokensToCookies(response, accessToken, refreshToken);
    }
}


