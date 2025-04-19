package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.dgi.department_assistant.domain.dto.user.UserDto;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.mapper.UserMapper;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;
import ru.nsu.dgi.department_assistant.domain.service.SecurityService;

/**
 * Реализация сервиса для работы с безопасностью и текущим пользователем
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public String getCurrentUserEmail() {
        CustomOAuth2User user = getCurrentUser();
        return user != null ? user.getEmail() : "noreply@example.com";
    }

    @Override
    public Long getCurrentUserId() {
        CustomOAuth2User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    @Override
    public Users.Role getCurrentUserRole() {
        CustomOAuth2User user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    @Override
    public CustomOAuth2User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomOAuth2User oauth2User) {
            return oauth2User;
        }
        log.warn("Текущий пользователь не найден или не является CustomOAuth2User");
        return null;
    }

    @Override
    public UserDto getUserFromDatabase(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public CustomOAuth2User createCustomOAuth2User(UserDto userDto, String email) {
        return new CustomOAuth2User(
                new DefaultOAuth2User(
                        List.of(new SimpleGrantedAuthority("ROLE_" + userDto.getRole().name())),
                        Map.of("email", email),
                        "email"
                ),
                userDto.getId(),
                userDto.getRole()
        );
    }
} 