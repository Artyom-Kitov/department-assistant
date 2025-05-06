package ru.nsu.dgi.department_assistant.domain.service.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.nsu.dgi.department_assistant.domain.service.impl.CustomAuthenticationServiceImpl;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final CustomAuthenticationServiceImpl authenticationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (!authenticationService.isOAuth2Authentication(authentication)) {
            return;
        }

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauth2Token.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        log.info("Processing OAuth2 success for user: {}", email);

        try {
            processOAuth2Success(oauth2Token, email, name, response);
        } catch (Exception e) {
            log.error("Failed to process OAuth2 success: {}", e.getMessage(), e);
        }

        redirectToSwagger(response);
    }

    private void processOAuth2Success(OAuth2AuthenticationToken oauth2Token, 
                                    String email, 
                                    String name, 
                                    HttpServletResponse response) {
        OAuth2AuthorizedClient client = authenticationService.loadAuthorizedClient(oauth2Token);
        
        if (client != null) {
            authenticationService.handleAuthorizedClient(client, email, name, response);
        } else {
            log.error("No OAuth2 client found for user: {}", email);
        }
    }

    private void redirectToSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
    }
}