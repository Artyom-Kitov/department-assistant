package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl{

    private final JwtTokenProviderServiceImpl tokenProvider;
    private final UserRepository userRepository;
    private final CookieServiceImpl cookieService;

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.extractTokenFromCookies(request, "refreshToken")
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomOAuth2User authUser = new CustomOAuth2User(
                new DefaultOAuth2User(
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                        Map.of("email", email),
                        "email"
                ),
                user.getId(),
                user.getRole()
        );

        String newAccessToken = tokenProvider.generateAccessToken(authUser);
        cookieService.addCookie(response, "accessToken", newAccessToken, 60 * 15);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.deleteCookie(request, response, "accessToken");
        cookieService.deleteCookie(request, response, "refreshToken");
        SecurityContextHolder.clearContext();

        //нужно решить че делать с этим
//        try {
//            response.sendRedirect("/");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
