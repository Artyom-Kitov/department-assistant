package ru.nsu.dgi.department_assistant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OAuth2ClientConfig {
    private static final String REGISTRATION_ID = "google";
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;
    
    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private String[] scopes;
    
    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String userInfoUri;

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
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(redirectUri)
                .scope(scopes)
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth?access_type=offline&prompt=consent")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri(userInfoUri)
                .userNameAttributeName("sub")
                .build();
    }
} 