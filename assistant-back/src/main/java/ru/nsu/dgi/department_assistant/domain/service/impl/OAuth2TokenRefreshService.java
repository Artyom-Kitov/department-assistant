package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2TokenRefreshService {
    private final UserRepository userRepository;
    private static final String GOOGLE_REGISTRATION_ID = "google";
    
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CookieServiceImpl cookieService;
    private final WebClient webClient = WebClient.builder().build();

    @Value("${oauth2.refreshCheckInterval}")
    private long refreshCheckInterval;

    @Value("${oauth2.refreshThreshold}")
    private long refreshThreshold;


    @Scheduled(fixedRateString = "${oauth2.refreshCheckInterval}")
    public void checkAndRefreshTokens() {
        log.debug("Starting scheduled OAuth2 token refresh check");
        List<Users> users = userRepository.findAll();

        for (Users user : users) {
            try {
                String email = user.getEmail();
                OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(GOOGLE_REGISTRATION_ID, email);

                if (client == null) {
                    log.debug("No OAuth2 client found for user: {}", email);
                    continue;
                }

                OAuth2AccessToken accessToken = client.getAccessToken();
                if (accessToken == null) {
                    log.debug("No access token found for user: {}", email);
                    continue;
                }

                if (isTokenExpiringSoon(accessToken)) {
                    log.info("OAuth2 access token for user {} is expiring soon, refreshing...", email);
                    refreshAccessToken(email);
                }
            } catch (Exception e) {
                log.error("Failed to refresh OAuth2 tokens for user: {}", user.getEmail(), e);
            }
        }
    }

    public OAuth2AccessToken refreshAccessToken(String email) {
        log.info("Refreshing OAuth access token for user: {}", email);

        OAuth2AuthorizedClient client = getAuthorizedClient(email);
        if (client == null) {
            return null;
        }

        OAuth2RefreshToken refreshToken = client.getRefreshToken();
        if (refreshToken == null) {
            log.warn("No refresh token available for user: {}", email);
            return null;
        }

        ClientRegistration registration = getGoogleClientRegistration();

        OAuth2AccessTokenResponse response = requestTokenRefresh(registration, refreshToken);
        if (response == null) {
            return null;
        }

        OAuth2AccessToken newAccessToken = response.getAccessToken();
        log.info("Successfully refreshed access token for user: {}", email);

        updateAuthorizedClient(registration, email, newAccessToken, refreshToken);
        return newAccessToken;
    }

    private OAuth2AuthorizedClient getAuthorizedClient(String email) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(GOOGLE_REGISTRATION_ID, email);
        if (client == null) {
            log.warn("No authorized client found for user: {}", email);
        }
        return client;
    }

    private OAuth2AccessTokenResponse requestTokenRefresh(ClientRegistration registration, OAuth2RefreshToken refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2ParameterNames.GRANT_TYPE, "refresh_token");
        formData.add(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken.getTokenValue());
        formData.add("client_id", registration.getClientId());
        formData.add("client_secret", registration.getClientSecret());

        return webClient.post()
                .uri(registration.getProviderDetails().getTokenUri())
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OAuth2AccessTokenResponse.class)
                .block();
    }

    private void updateAuthorizedClient(ClientRegistration registration, String email, 
                                      OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        OAuth2AuthorizedClient newClient = new OAuth2AuthorizedClient(
                registration,
                email,
                accessToken,
                refreshToken
        );
        authorizedClientService.saveAuthorizedClient(newClient, null);
    }

    public boolean isTokenExpiringSoon(OAuth2AccessToken token) {
        if (token == null || token.getExpiresAt() == null) {
            return false;
        }
        Instant now = Instant.now();
        Instant expiresAt = token.getExpiresAt();
        return expiresAt.minusMillis(refreshThreshold).isBefore(now);
    }

    public void updateTokensInCookies(HttpServletResponse response, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        cookieService.addOAuth2TokensToCookies(response, accessToken, refreshToken);
    }

    public Optional<OAuth2AccessToken> getAccessTokenFromCookies(HttpServletRequest request) {
        return cookieService.getOAuth2AccessTokenFromCookies(request)
                .map(this::createAccessToken);
    }

    public Optional<OAuth2RefreshToken> getRefreshTokenFromCookies(HttpServletRequest request) {
        return cookieService.getOAuth2RefreshTokenFromCookies(request)
                .map(this::createRefreshToken);
    }

    public void restoreOAuth2Authorization(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User user) {
            String email = user.getEmail();

            if (authorizedClientService.loadAuthorizedClient(GOOGLE_REGISTRATION_ID, email) != null) {
                return;
            }

            Optional<String> oauth2AccessToken = cookieService.getOAuth2AccessTokenFromCookies(request);
            Optional<String> oauth2RefreshToken = cookieService.getOAuth2RefreshTokenFromCookies(request);

            oauth2AccessToken.ifPresent(s -> restoreOAuth2Client(email, user, s, oauth2RefreshToken.orElse(null)));
        }
    }

    private void restoreOAuth2Client(String email, CustomOAuth2User user, String accessTokenValue, String refreshTokenValue) {
        ClientRegistration googleRegistration = getGoogleClientRegistration();
        OAuth2AccessToken accessToken = createAccessToken(accessTokenValue);
        OAuth2RefreshToken refreshToken = createRefreshToken(refreshTokenValue);
        OAuth2AuthorizedClient client = createOAuth2AuthorizedClient(googleRegistration, email, accessToken, refreshToken);
        OAuth2AuthenticationToken oauth2Token = createOAuth2AuthenticationToken(user, email);
        
        authorizedClientService.saveAuthorizedClient(client, oauth2Token);
    }

    private ClientRegistration getGoogleClientRegistration() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId(GOOGLE_REGISTRATION_ID);
        if (googleRegistration == null) {
            throw new RuntimeException("Google client registration not found");
        }
        return googleRegistration;
    }

    private OAuth2AccessToken createAccessToken(String tokenValue) {
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                tokenValue,
                Instant.now(),
                Instant.now().plusSeconds(3600)); // 1 hour
    }

    private OAuth2RefreshToken createRefreshToken(String tokenValue) {
        if (tokenValue == null) {
            return null;
        }
        return new OAuth2RefreshToken(tokenValue, Instant.now());
    }

    private OAuth2AuthorizedClient createOAuth2AuthorizedClient(
            ClientRegistration registration,
            String email,
            OAuth2AccessToken accessToken,
            OAuth2RefreshToken refreshToken) {
        return new OAuth2AuthorizedClient(
                registration,
                email,
                accessToken,
                refreshToken);
    }

    private OAuth2AuthenticationToken createOAuth2AuthenticationToken(CustomOAuth2User user, String email) {
        Map<String, Object> attributes = createOAuth2UserAttributes(user, email);
        DefaultOAuth2User oauth2User = createDefaultOAuth2User(user, attributes);
        
        return new OAuth2AuthenticationToken(
                oauth2User,
                user.getAuthorities(),
                GOOGLE_REGISTRATION_ID
        );
    }

    private Map<String, Object> createOAuth2UserAttributes(CustomOAuth2User user, String email) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", user.getName());
        attributes.put("sub", email);
        return attributes;
    }

    private DefaultOAuth2User createDefaultOAuth2User(CustomOAuth2User user, Map<String, Object> attributes) {
        return new DefaultOAuth2User(
                user.getAuthorities(),
                attributes,
                "email"
        );
    }

    public Map<String, Object> getOAuth2Status(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User user) {
            String email = user.getEmail();

            Map<String, Object> oauth2Info = getOAuth2Info(request, email);
            
            response.put("authenticated", true);
            response.put("email", email);
            response.put("name", user.getName());
            response.put("role", user.getRole().name());
            response.put("oauth2Info", oauth2Info);
        } else {
            response.put("authenticated", false);
            response.put("oauth2Info", Map.of(
                    "authorized", false,
                    "message", "Not authenticated"
            ));
        }

        return response;
    }

    private Map<String, Object> getOAuth2Info(HttpServletRequest request, String email) {
        Map<String, Object> oauth2Info = new HashMap<>();
        
        Optional<String> oauth2AccessToken = cookieService.getOAuth2AccessTokenFromCookies(request);
        Optional<String> oauth2RefreshToken = cookieService.getOAuth2RefreshTokenFromCookies(request);

        oauth2Info.put("hasAccessToken", oauth2AccessToken.isPresent());
        oauth2Info.put("hasRefreshToken", oauth2RefreshToken.isPresent());

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(GOOGLE_REGISTRATION_ID, email);
        if (client != null) {
            addClientInfoToResponse(oauth2Info, client);
        } else {
            oauth2Info.put("authorized", false);
            oauth2Info.put("message", "No OAuth2 authorization found in service");
        }

        return oauth2Info;
    }

    private void addClientInfoToResponse(Map<String, Object> oauth2Info, OAuth2AuthorizedClient client) {
        OAuth2AccessToken accessToken = client.getAccessToken();
        Instant expiresAt = accessToken.getExpiresAt();
        boolean isExpired = expiresAt != null && expiresAt.isBefore(Instant.now());

        oauth2Info.put("authorized", true);
        oauth2Info.put("expired", isExpired);
        oauth2Info.put("expiresAt", expiresAt);
        oauth2Info.put("tokenType", accessToken.getTokenType().getValue());
        oauth2Info.put("scopes", accessToken.getScopes());
    }
} 