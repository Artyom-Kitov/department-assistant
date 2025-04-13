package ru.nsu.dgi.department_assistant.domain.service.handler;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.service.impl.CookieServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.JwtTokenProviderServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.TokenStorageServiceImpl;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProviderServiceImpl tokenProvider;
    private final CookieServiceImpl cookieService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final TokenStorageServiceImpl tokenStorageService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();

        // 1. Генерация JWT токенов
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        // 2. Безопасное хранение refresh токена
        cookieService.addCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 7); // 7 дней

        // 3. Возвращаем access token в теле ответа и в secure куке
        response.addHeader("Set-Cookie",
                "accessToken=" + accessToken +
                        "; Path=/; Secure; HttpOnly; SameSite=Strict; Max-Age=900"); // 15 мин

        // 4. Сохраняем OAuth токены Google только на сервере
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName());

            // Сохраняем в базе данных или серверном кеше
            tokenStorageService.storeGoogleTokens(
                    user.getEmail(),
                    client.getAccessToken(),
                    client.getRefreshToken()
            );
        }

        // 5. Перенаправление с токенами
        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/auth/callback")
                .queryParam("access_token", accessToken)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
//
//        String accessToken = tokenProvider.generateAccessToken(user);
//        String refreshToken = tokenProvider.generateRefreshToken(user);
//
//        cookieService.addCookie(response, "accessToken", accessToken, 60 * 15); // 15 мин
//        cookieService.addCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 7); // 7 дней
//
//        // Store OAuth2 authorization for Gmail API
//        if (authentication instanceof OAuth2AuthenticationToken) {
//            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
//            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
//                    oauth2Token.getAuthorizedClientRegistrationId(),
//                    user.getEmail());
//
//            if (authorizedClient != null) {
//                log.info("OAuth2 authorization found for user: {}", user.getEmail());
//
//                // Save OAuth2 authorization for Gmail API
//                try {
//                    authorizedClientService.saveAuthorizedClient(authorizedClient, oauth2Token);
//                    log.info("OAuth2 authorization saved for user: {}", user.getEmail());
//
//                    // Save OAuth2 tokens in cookies
//                    if (authorizedClient.getAccessToken() != null) {
//                        cookieService.addCookie(response, "oauth2_access_token",
//                                authorizedClient.getAccessToken().getTokenValue(),
//                                60 * 60); // 1 hour
//                        log.info("OAuth2 access token saved in cookies for user: {}", user.getEmail());
//                    }
//
//                    if (authorizedClient.getRefreshToken() != null) {
//                        cookieService.addCookie(response, "oauth2_refresh_token",
//                                authorizedClient.getRefreshToken().getTokenValue(),
//                                60 * 60 * 24 * 7); // 7 days
//                        log.info("OAuth2 refresh token saved in cookies for user: {}", user.getEmail());
//                    }
//                } catch (Exception e) {
//                    log.error("Failed to save OAuth2 authorization for user: {}", user.getEmail(), e);
//                }
//            } else {
//                log.warn("No OAuth2 authorization found for user: {}", user.getEmail());
//            }
//        } else {
//            log.warn("Authentication is not OAuth2AuthenticationToken: {}", authentication.getClass());
//        }
//
//        // response.setStatus(HttpStatus.OK.value());
//        response.sendRedirect("http://localhost:8080/swagger-ui/index.html"); // фронт подхватит, что всё ок
//    }
}
