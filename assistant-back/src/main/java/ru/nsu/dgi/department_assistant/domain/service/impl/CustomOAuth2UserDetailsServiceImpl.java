package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.dto.user.UserDto;
import ru.nsu.dgi.department_assistant.domain.entity.users.CustomOAuth2User;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserDetailsServiceImpl extends DefaultOAuth2UserService {
    private final SecurityServiceImpl securityService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oauthUser = super.loadUser(request);
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        UserDto userDto = securityService.findOrCreateUser(email, name);
        return new CustomOAuth2User(oauthUser, userDto.getId(), userDto.getRole());
    }
}
