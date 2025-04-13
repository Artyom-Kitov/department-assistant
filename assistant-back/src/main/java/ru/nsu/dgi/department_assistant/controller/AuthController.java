package ru.nsu.dgi.department_assistant.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;
import ru.nsu.dgi.department_assistant.domain.service.impl.AuthServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.CookieServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.TokenStorageServiceImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final TokenStorageServiceImpl tokenStorage;
    private final AuthServiceImpl authService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final CookieServiceImpl cookieService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            authService.refreshToken(request, response);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/success")
    public ResponseEntity<?> loginSuccess() {
        return ResponseEntity.ok().build();
    }
    @GetMapping("/token")
    public ResponseEntity<?> getGoogleToken(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomOAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        Optional<TokenStorageServiceImpl.OAuth2Tokens> tokens = tokenStorage.getGoogleTokens(user.getEmail());

        return tokens.map(t -> ResponseEntity.ok()
                        .body(Map.of(
                                "access_token", t.accessToken().getTokenValue(),
                                "expires_in", Duration.between(Instant.now(), t.accessToken().getExpiresAt()).getSeconds()
                        )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/oauth2/restore")
    public ResponseEntity<?> restoreOAuth2Authorization(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
            String email = user.getEmail();

            // Check if OAuth2 authorization already exists
            OAuth2AuthorizedClient existingClient = authorizedClientService.loadAuthorizedClient("google", email);
            if (existingClient != null) {
                return ResponseEntity.ok().build();
            }

            // Get tokens from cookies
            Optional<String> oauth2AccessToken = cookieService.extractTokenFromCookies(request, "oauth2_access_token");
            Optional<String> oauth2RefreshToken = cookieService.extractTokenFromCookies(request, "oauth2_refresh_token");

            if (oauth2AccessToken.isPresent()) {
                // Get the Google client registration
                ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
                if (googleRegistration == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Google client registration not found");
                }

                // Create OAuth2AccessToken
                OAuth2AccessToken accessToken = new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        oauth2AccessToken.get(),
                        Instant.now(),
                        Instant.now().plusSeconds(3600));

                // Create OAuth2RefreshToken if available
                OAuth2RefreshToken refreshToken = null;
                if (oauth2RefreshToken.isPresent()) {
                    refreshToken = new OAuth2RefreshToken(oauth2RefreshToken.get(), Instant.now());
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
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/oauth2/status")
    public ResponseEntity<Map<String, Object>> getOAuth2Status(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
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
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }
}
