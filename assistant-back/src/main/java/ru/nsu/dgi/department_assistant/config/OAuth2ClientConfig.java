package ru.nsu.dgi.department_assistant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OAuth2ClientConfig {
    private static final String REGISTRATION_ID = "google";
    private static final String CLIENT_ID = "539777559204-07npafenqtct9sqgvbc9f2ba1p7d7d72.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String BASE_URL = "http://localhost:8080";
    private static final String REDIRECT_URI = BASE_URL + "/login/oauth2/code/" + REGISTRATION_ID;
    private static final String AUTHORIZATION_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URI = "https://www.googleapis.com/oauth2/v3/userinfo";

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration registration = googleClientRegistration();
        log.info("OAuth2 Client Registration configured:");
        log.info("Registration ID: {}", registration.getRegistrationId());
        log.info("Client ID: {}", registration.getClientId());
        log.info("Redirect URI: {}", registration.getRedirectUri());
        log.info("Authorization URI: {}", registration.getProviderDetails().getAuthorizationUri());
        log.info("Token URI: {}", registration.getProviderDetails().getTokenUri());
        log.info("User Info URI: {}", registration.getProviderDetails().getUserInfoEndpoint().getUri());
        log.info("Scopes: {}", registration.getScopes());
        return new InMemoryClientRegistrationRepository(registration);
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId(REGISTRATION_ID)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(REDIRECT_URI)
                .scope("email", "profile", "https://www.googleapis.com/auth/gmail.send", "https://www.googleapis.com/auth/gmail.compose")
                .authorizationUri(AUTHORIZATION_URI + "?access_type=offline&prompt=consent")
                .tokenUri(TOKEN_URI)
                .userInfoUri(USER_INFO_URI)
                .userNameAttributeName("sub")
                .build();
    }
} 