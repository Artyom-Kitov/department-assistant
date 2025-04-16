package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
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
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.extractTokenFromCookies(request, "refreshToken")
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomOAuth2User authUser = new CustomOAuth2User(
                new DefaultOAuth2User(
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                        Map.of("email", email),
                        "email"
                ),
                user.getId(),
                user.getRole()
        );

        String newAccessToken = tokenProvider.generateAccessToken(authUser);
        cookieService.addCookie(response, "accessToken", newAccessToken, 60 * 15);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.deleteCookie(request, response, "accessToken");
        cookieService.deleteCookie(request, response, "refreshToken");
        SecurityContextHolder.clearContext();
    }

    public void restoreOAuth2Authorization(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
            String email = user.getEmail();

            // Check if OAuth2 authorization already exists
            OAuth2AuthorizedClient existingClient = authorizedClientService.loadAuthorizedClient("google", email);
            if (existingClient != null) {
                return;
            }

            // Get tokens from cookies
            Optional<String> oauth2AccessToken = cookieService.extractTokenFromCookies(request, "oauth2_access_token");
            Optional<String> oauth2RefreshToken = cookieService.extractTokenFromCookies(request, "oauth2_refresh_token");

            if (oauth2AccessToken.isPresent()) {
                restoreOAuth2Client(email, user, oauth2AccessToken.get(), oauth2RefreshToken.orElse(null));
            }
        }
    }

    private void restoreOAuth2Client(String email, CustomOAuth2User user, String accessTokenValue, String refreshTokenValue) {
        // Get the Google client registration
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        if (googleRegistration == null) {
            throw new RuntimeException("Google client registration not found");
        }

        // Create OAuth2AccessToken
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                accessTokenValue,
                Instant.now(),
                Instant.now().plusSeconds(3600));

        // Create OAuth2RefreshToken if available
        OAuth2RefreshToken refreshToken = null;
        if (refreshTokenValue != null) {
            refreshToken = new OAuth2RefreshToken(refreshTokenValue, Instant.now());
        }

        // Create OAuth2AuthorizedClient
        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(
                googleRegistration,
                email,
                accessToken,
                refreshToken);

        // Create OAuth2AuthenticationToken
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", user.getName());
        attributes.put("sub", email);

        DefaultOAuth2User oauth2User = new DefaultOAuth2User(
                user.getAuthorities(),
                attributes,
                "email"
        );

        OAuth2AuthenticationToken oauth2Token = new OAuth2AuthenticationToken(
                oauth2User,
                user.getAuthorities(),
                "google"
        );

        // Save the client
        authorizedClientService.saveAuthorizedClient(client, oauth2Token);
    }

    public Map<String, Object> getOAuth2Status(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
            String email = user.getEmail();

            // Check OAuth2 tokens in cookies
            Optional<String> oauth2AccessToken = cookieService.extractTokenFromCookies(request, "oauth2_access_token");
            Optional<String> oauth2RefreshToken = cookieService.extractTokenFromCookies(request, "oauth2_refresh_token");

            response.put("authenticated", true);
            response.put("email", email);
            response.put("name", user.getName());
            response.put("role", user.getRole().name());

            // Check if OAuth2 authorization exists in service
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", email);

            Map<String, Object> oauth2Info = new HashMap<>();
            oauth2Info.put("hasAccessToken", oauth2AccessToken.isPresent());
            oauth2Info.put("hasRefreshToken", oauth2RefreshToken.isPresent());

            if (client != null) {
                OAuth2AccessToken accessToken = client.getAccessToken();
                Instant expiresAt = accessToken.getExpiresAt();
                boolean isExpired = expiresAt != null && expiresAt.isBefore(Instant.now());

                oauth2Info.put("authorized", true);
                oauth2Info.put("expired", isExpired);
                oauth2Info.put("expiresAt", expiresAt);
                oauth2Info.put("tokenType", accessToken.getTokenType().getValue());
                oauth2Info.put("scopes", accessToken.getScopes());
            } else {
                oauth2Info.put("authorized", false);
                oauth2Info.put("message", "No OAuth2 authorization found in service");
            }

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
}
