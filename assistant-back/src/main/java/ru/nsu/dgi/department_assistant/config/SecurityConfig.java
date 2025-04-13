package ru.nsu.dgi.department_assistant.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import ru.nsu.dgi.department_assistant.domain.service.handler.OAuth2SuccessHandler;
import ru.nsu.dgi.department_assistant.domain.service.impl.CustomOAuth2UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomOAuth2UserDetailsServiceImpl customUserDetailsService;
    private final OAuth2SuccessHandler oauth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/auth/logout") // CSRF не нужен для JWT
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/auth/**",
                                "/oauth2/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui/oauth2-redirect.html",
                                "/login/oauth2/code/google"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customUserDetailsService)
                        )
                        .successHandler(oauth2SuccessHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .securityContext(securityContext -> securityContext
//                        .securityContextRepository(new DelegatingSecurityContextRepository(
//                                new RequestHeaderSecurityContextRepository(), // Для API
//                                new CookieSecurityContextRepository()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}