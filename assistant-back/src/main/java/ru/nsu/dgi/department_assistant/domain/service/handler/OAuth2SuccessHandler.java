package ru.nsu.dgi.department_assistant.domain.service.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.service.impl.AuthServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.CookieServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.JwtTokenProviderServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.OAuth2TokenRefreshService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProviderServiceImpl tokenProvider;
    private final CookieServiceImpl cookieService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final AuthServiceImpl authService;
    private final OAuth2TokenRefreshService oauth2Service;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        cookieService.addAccessTokenCookie(response, accessToken);
        cookieService.addRefreshTokenCookie(response, refreshToken);

        // Store OAuth2 authorization for Gmail API
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            
            try {
                // First save the authorized client from the authentication
                OAuth2AuthorizedClient currentClient = authorizedClientService.loadAuthorizedClient(
                    oauth2Token.getAuthorizedClientRegistrationId(),
                    oauth2Token.getName());

                if (currentClient != null) {
                    // Save OAuth2 tokens in cookies
                    oauth2Service.updateTokensInCookies(response, 
                        currentClient.getAccessToken(), 
                        currentClient.getRefreshToken());

                    // Save the authorized client
                    authorizedClientService.saveAuthorizedClient(currentClient, oauth2Token);
                    log.info("OAuth2 authorization saved for user: {}", user.getEmail());
                } else {
                    log.warn("No OAuth2 authorization available for user: {}", user.getEmail());
                }
            } catch (Exception e) {
                log.error("Failed to save OAuth2 authorization for user: {}", user.getEmail(), e);
            }
        } else {
            log.warn("Authentication is not OAuth2AuthenticationToken: {}", authentication.getClass());
        }

        // Automatically restore OAuth2 authorization
        try {
            authService.restoreOAuth2Authorization(request);
            log.info("OAuth2 authorization automatically restored for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to automatically restore OAuth2 authorization for user: {}", user.getEmail(), e);
        }

        response.sendRedirect("http://localhost:8080/swagger-ui/index.html"); // фронт подхватит, что всё ок
    }
}