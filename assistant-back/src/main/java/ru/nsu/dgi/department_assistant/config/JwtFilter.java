package ru.nsu.dgi.department_assistant.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.service.impl.JwtTokenProviderServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProviderServiceImpl jwtTokenProviderService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromCookies(request, "accessToken");

        if (token != null && jwtTokenProviderService.validateToken(token)) {
            String email = jwtTokenProviderService.getEmailFromToken(token);
            Users.Role role = jwtTokenProviderService.getRoleFromToken(token);
            Long userId = jwtTokenProviderService.getUserIdFromToken(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomOAuth2User user = new CustomOAuth2User(
                        new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority("ROLE_" + role.name())),
                                Map.of("email", email), "email"),
                                userId,
                                role
                );

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
