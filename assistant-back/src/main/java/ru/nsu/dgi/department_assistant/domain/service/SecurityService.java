package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.user.UserDto;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;

/**
 * Сервис для работы с безопасностью и текущим пользователем
 */
public interface SecurityService {
    
    /**
     * Получает email текущего пользователя
     * @return email текущего пользователя
     */
    String getCurrentUserEmail();

    /**
     * Получает ID текущего пользователя
     * @return ID текущего пользователя
     */
    Long getCurrentUserId();
    
    /**
     * Получает роль текущего пользователя
     * @return роль текущего пользователя
     */
    Users.Role getCurrentUserRole();
    
    /**
     * Получает объект текущего пользователя
     * @return объект текущего пользователя
     */
    CustomOAuth2User getCurrentUser();

    /**
     * Получает пользователя из базы данных по email
     * @param email email пользователя
     * @return пользователь из базы данных
     */
    UserDto getUserFromDatabase(String email);

    /**
     * Создает объект CustomOAuth2User для пользователя
     * @param userDto пользователь
     * @param email email пользователя
     * @return объект CustomOAuth2User
     */
    CustomOAuth2User createCustomOAuth2User(UserDto userDto, String email);
} 