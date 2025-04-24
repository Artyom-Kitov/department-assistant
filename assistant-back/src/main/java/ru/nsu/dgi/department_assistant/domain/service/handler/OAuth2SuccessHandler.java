package ru.nsu.dgi.department_assistant.domain.service.handler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;
import ru.nsu.dgi.department_assistant.domain.service.impl.CookieServiceImpl;
import ru.nsu.dgi.department_assistant.domain.service.impl.OAuth2TokenRefreshService;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
//import ru.nsu.dgi.department_assistant.domain.service.impl.AuthServiceImpl;
//import ru.nsu.dgi.department_assistant.domain.service.impl.CookieServiceImpl;
//import ru.nsu.dgi.department_assistant.domain.service.impl.JwtTokenProviderServiceImpl;
//import ru.nsu.dgi.department_assistant.domain.service.impl.OAuth2TokenRefreshService;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
//
//    private final JwtTokenProviderServiceImpl tokenProvider;
//    private final CookieServiceImpl cookieService;
//    private final OAuth2AuthorizedClientService authorizedClientService;
//    private final AuthServiceImpl authService;
//    private final OAuth2TokenRefreshService oauth2Service;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
//
//        String accessToken = tokenProvider.generateAccessToken(user);
//        String refreshToken = tokenProvider.generateRefreshToken(user);
//
//        cookieService.addAccessTokenCookie(response, accessToken);
//        cookieService.addRefreshTokenCookie(response, refreshToken);
//
//        // Store OAuth2 authorization for Gmail API
//        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
//
//            try {
//                // First save the authorized client from the authentication
//                OAuth2AuthorizedClient currentClient = authorizedClientService.loadAuthorizedClient(
//                    oauth2Token.getAuthorizedClientRegistrationId(),
//                    oauth2Token.getName());
//
//                if (currentClient != null) {
//                    // Save OAuth2 tokens in cookies
//                    oauth2Service.updateTokensInCookies(response,
//                        currentClient.getAccessToken(),
//                        currentClient.getRefreshToken());
//
//                    // Save the authorized client
//                    authorizedClientService.saveAuthorizedClient(currentClient, oauth2Token);
//                    log.info("OAuth2 authorization saved for user: {}", user.getEmail());
//                } else {
//                    log.warn("No OAuth2 authorization available for user: {}", user.getEmail());
//                }
//            } catch (Exception e) {
//                log.error("Failed to save OAuth2 authorization for user: {}", user.getEmail(), e);
//            }
//        } else {
//            log.warn("Authentication is not OAuth2AuthenticationToken: {}", authentication.getClass());
//        }
//
//        // Automatically restore OAuth2 authorization
//        try {
//            authService.restoreOAuth2Authorization(request);
//            log.info("OAuth2 authorization automatically restored for user: {}", user.getEmail());
//        } catch (Exception e) {
//            log.error("Failed to automatically restore OAuth2 authorization for user: {}", user.getEmail(), e);
//        }
//
//        response.sendRedirect("http://localhost:8080/swagger-ui/index.html"); // фронт подхватит, что всё ок
//    }
//}
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2TokenRefreshService oauth2Service;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (!(authentication instanceof OAuth2AuthenticationToken oauth2Token)) {
            log.error("Authentication is not OAuth2AuthenticationToken");
            return;
        }

        OAuth2User oauth2User = oauth2Token.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        log.info("Processing OAuth2 success for user: {}", email);

        try {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauth2Token.getAuthorizedClientRegistrationId(),
                    oauth2Token.getName()
            );

            if (client != null) {
                log.info("Found OAuth2 client for user: {}", email);

                // Сохраняем OAuth2 токены в куки
                oauth2Service.updateTokensInCookies(
                        response,
                        client.getAccessToken(),
                        client.getRefreshToken()
                );
                log.info("Updated OAuth2 tokens in cookies for user: {}", email);

                // Проверяем/создаем пользователя в БД
                Users user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            Users newUser = new Users();
                            newUser.setEmail(email);
                            newUser.setName(name);
                            newUser.setRole(Users.Role.USER);
                            return userRepository.save(newUser);
                        });
                log.info("User found/created in DB with role: {}", user.getRole());

                // Создаем CustomOAuth2User с правильными ролями
                Collection<GrantedAuthority> authorities = Collections.singleton(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                );

                CustomOAuth2User customUser = new CustomOAuth2User(
                        oauth2User,
                        user.getId(),
                        user.getRole()
                );

                // Обновляем аутентификацию
                OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                        customUser,
                        authorities,
                        oauth2Token.getAuthorizedClientRegistrationId()
                );

                SecurityContextHolder.getContext().setAuthentication(newAuth);
                log.info("Updated authentication in SecurityContext for user: {} with role: {}",
                        email, user.getRole());

                // Проверяем текущую аутентификацию
                Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                log.info("Current authentication: {}", currentAuth);
                log.info("Current authorities: {}", currentAuth.getAuthorities());
            } else {
                log.error("No OAuth2 client found for user: {}", email);
            }
        } catch (Exception e) {
            log.error("Failed to process OAuth2 success: {}", e.getMessage(), e);
        }

        // Редирект на Swagger UI
        response.sendRedirect("/swagger-ui/index.html");
    }
}