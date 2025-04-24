package ru.nsu.dgi.department_assistant.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;
import ru.nsu.dgi.department_assistant.domain.service.impl.CookieServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.OAuth2TokenRefreshService;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2TokenAuthenticationFilter extends OncePerRequestFilter {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2TokenRefreshService tokenService;
    private final UserRepository userRepository;
    private final CookieServiceImpl cookieService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Пропускаем публичные эндпоинты
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/oauth2") ||
                path.startsWith("/login");
    }


        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            log.debug("=== Starting OAuth2 Filter for path: {} ===", request.getRequestURI());

            try {
                // Сначала пробуем получить access token
                Optional<String> accessToken = cookieService.getOAuth2AccessTokenFromCookies(request);

                if (accessToken.isPresent()) {
                    processAccessToken(accessToken.get(), request, response);
                } else {
                    // Если access token отсутствует, пробуем использовать refresh token
                    Optional<String> refreshToken = cookieService.getOAuth2RefreshTokenFromCookies(request);
                    if (refreshToken.isPresent()) {
                        processRefreshToken(refreshToken.get(), response);
                    }
                }
            } catch (Exception e) {
                log.error("Authentication error", e);
                SecurityContextHolder.clearContext();
            }

            filterChain.doFilter(request, response);
        }

        private void processAccessToken(String accessToken, HttpServletRequest request, HttpServletResponse response) {
            OAuth2AuthorizedClient client = findAuthorizedClientByToken(accessToken);

            if (client != null) {
                String email = client.getPrincipalName();
                log.debug("Found client for email: {}", email);

                // Проверяем, не истекает ли токен
                if (tokenService.isTokenExpiringSoon(client.getAccessToken())) {
                    log.debug("Access token is expiring soon, attempting refresh");
                    OAuth2AccessToken newToken = tokenService.refreshAccessToken(email, response);
                    if (newToken != null) {
                        client = authorizedClientService.loadAuthorizedClient("google", email);
                    }
                }

                updateSecurityContext(email, client);
            }
        }

        private void processRefreshToken(String refreshToken, HttpServletResponse response) {
            try {
                // Находим клиента по refresh token
                OAuth2AuthorizedClient client = findClientByRefreshToken(refreshToken);

                if (client != null) {
                    String email = client.getPrincipalName();
                    log.debug("Found client by refresh token for email: {}", email);

                    // Пробуем получить новый access token
                    OAuth2AccessToken newToken = tokenService.refreshAccessToken(email, response);
                    if (newToken != null) {
                        client = authorizedClientService.loadAuthorizedClient("google", email);
                        updateSecurityContext(email, client);
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

        private void updateSecurityContext(String email, OAuth2AuthorizedClient client) {
            try {
                Users user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

                Authentication auth = createAuthentication(user, client);
                SecurityContextHolder.getContext().setAuthentication(auth);

                log.debug("Successfully authenticated user: {}", email);
            } catch (Exception e) {
                log.error("Failed to update security context", e);
                throw e;
            }
        }


        private Authentication createAuthentication(Users user, OAuth2AuthorizedClient client) {
            Collection<GrantedAuthority> authorities = Collections.singleton(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("email", user.getEmail()); // Важно: используем email
            attributes.put("name", user.getName());
            attributes.put("sub", user.getEmail()); // sub тоже должен быть email

            DefaultOAuth2User oauth2User = new DefaultOAuth2User(
                    authorities,
                    attributes,
                    "email" // Указываем, что nameAttributeKey это email
            );

            CustomOAuth2User customUser = new CustomOAuth2User(
                    oauth2User,
                    user.getId(),
                    user.getRole()
            );

            return new OAuth2AuthenticationToken(
                    customUser,
                    authorities,
                    client.getClientRegistration().getRegistrationId()
            );
        }

        private OAuth2AuthorizedClient findAuthorizedClientByToken(String accessToken) {
            try {
                // Получаем все клиенты
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