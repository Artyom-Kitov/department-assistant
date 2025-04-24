package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserDetailsServiceImpl extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oauthUser = super.loadUser(request);
        String email = oauthUser.getAttribute("email");

        Users user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(oauthUser));

        return new CustomOAuth2User(oauthUser, user.getId(), user.getRole());
    }

    private Users createNewUser(OAuth2User oauthUser) {
        Users newUser = new Users();
        newUser.setEmail(oauthUser.getAttribute("email"));
        newUser.setName(oauthUser.getAttribute("name"));
        newUser.setRole(Users.Role.ADMIN); // Дефолтная роль USER
        return userRepository.save(newUser);
    }
}
