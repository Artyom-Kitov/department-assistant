package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.user.UserDto;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.exception.TokenRefreshException;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomAuthenticationServiceImpl {
    private static final String GOOGLE_REGISTRATION_ID = "google";
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final SecurityServiceImpl securityService;
    private final CookieServiceImpl cookieService;
    public boolean isOAuth2Authentication(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            log.error("Authentication is not OAuth2AuthenticationToken");
            return false;
        }
        return true;
    }
    public OAuth2AuthenticationToken createOAuth2AuthenticationToken(
            CustomOAuth2User customUser,
            Collection<GrantedAuthority> authorities,
            OAuth2AuthorizedClient client) {
        return new OAuth2AuthenticationToken(
                customUser,
                authorities,
                client.getClientRegistration().getRegistrationId()
        );
    }


    public Authentication createOAuth2AuthenticationToken(OAuth2User oauth2User, Collection<GrantedAuthority> authorities) {
        return new OAuth2AuthenticationToken(
                oauth2User,
                authorities,
                GOOGLE_REGISTRATION_ID
        );
    }
    public Authentication createAuthenticationForClient(String email) {
        UserDto userDto = securityService.findUserByEmail(email);
        Collection<GrantedAuthority> authorities = securityService.createAuthorities(userDto);
        OAuth2User oauth2User = securityService.createCustomOAuth2User(userDto, email);
        return createOAuth2AuthenticationToken(oauth2User, authorities);
    }

    public void logCurrentAuthentication() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current authentication: {}", currentAuth);
        log.info("Current authorities: {}", currentAuth.getAuthorities());
    }

    public void updateAuthentication(UserDto userDto, OAuth2AuthorizedClient client) {
        Collection<GrantedAuthority> authorities = securityService.createAuthorities(userDto);
        CustomOAuth2User customUser = securityService.createCustomOAuth2User(userDto, client.getPrincipalName());
        OAuth2AuthenticationToken newAuth = createOAuth2AuthenticationToken(customUser, authorities, client);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
        log.info("Updated authentication in SecurityContext for user: {} with role: {}",
                userDto.getEmail(), userDto.getRole());

        logCurrentAuthentication();
    }
    public OAuth2AuthorizedClient loadAuthorizedClient(OAuth2AuthenticationToken oauth2Token) {
        return authorizedClientService.loadAuthorizedClient(
                oauth2Token.getAuthorizedClientRegistrationId(),
                oauth2Token.getName()
        );
    }

    public void handleAuthorizedClient(OAuth2AuthorizedClient client,
                                        String email,
                                        String name,
                                        HttpServletResponse response) {
        log.info("Found OAuth2 client for user: {}", email);

        updateOAuth2TokensInCookies(client, response);
        UserDto userDto = securityService.findOrCreateUser(email, name);
        updateAuthentication(userDto, client);
    }


    public OAuth2AuthorizedClient getAndValidateAuthorizedClient(String email) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(GOOGLE_REGISTRATION_ID, email);
        if (client == null) {
            throw new TokenRefreshException("No authorized client found");
        }
        return client;
    }
    public OAuth2AuthorizedClient createNewAuthorizedClient(
            ClientRegistration registration,
            String email,
            OAuth2AccessToken accessToken,
            OAuth2RefreshToken refreshToken
    ) {
        return new OAuth2AuthorizedClient(
                registration,
                email,
                accessToken,
                refreshToken
        );
    }

    public void saveAuthorizedClient(String email, OAuth2AuthorizedClient newClient) {
        try {
            Authentication authentication = createAuthenticationForClient(email);
            authorizedClientService.saveAuthorizedClient(newClient, authentication);
            log.debug("Successfully saved new authorized client");
        } catch (Exception e) {
            log.error("Failed to save authorized client", e);
            throw new TokenRefreshException("Failed to save authorized client: " + e.getMessage());
        }
    }
    public ClientRegistration getAndValidateClientRegistration() {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(GOOGLE_REGISTRATION_ID);
        if (registration == null) {
            throw new TokenRefreshException("Google client registration not found");
        }
        return registration;
    }
    public void updateOAuth2TokensInCookies(OAuth2AuthorizedClient client, HttpServletResponse response) {
        cookieService.addOAuth2TokensToCookies(response, client.getAccessToken(), client.getRefreshToken());
        log.info("Updated OAuth2 tokens in cookies for user: {}", client.getPrincipalName());
    }
    public void updateSecurityContext(String email) {
        try {

            Authentication auth = createAuthenticationForClient(email);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("Successfully authenticated user: {}", email);
        } catch (Exception e) {
            log.error("Failed to update security context", e);
            throw e;
        }
    }

}

