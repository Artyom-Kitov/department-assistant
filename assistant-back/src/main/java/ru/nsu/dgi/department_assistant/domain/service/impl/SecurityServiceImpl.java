package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.service.SecurityService;

/**
 * Реализация сервиса для работы с безопасностью и текущим пользователем
 */
@Slf4j
@Service
public class SecurityServiceImpl implements SecurityService {

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
} 