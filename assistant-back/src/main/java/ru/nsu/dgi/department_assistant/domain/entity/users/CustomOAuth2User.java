package ru.nsu.dgi.department_assistant.domain.entity.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


    public class CustomOAuth2User implements OAuth2User {
        private final OAuth2User oauth2User;
        private final Long id;
        private final Users.Role role;

        public CustomOAuth2User(OAuth2User oauth2User, Long id, Users.Role role) {
            this.oauth2User = oauth2User;
            this.id = id;
            this.role = role;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return oauth2User.getAttributes();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        @Override
        public String getName() {
            return getEmail(); // Теперь возвращаем email вместо имени!
        }

        public String getEmail() {
            return oauth2User.getAttribute("email");
        }

        public String getDisplayName() {  // Добавим отдельный метод для получения имени
            return oauth2User.getAttribute("name");
        }

        public Long getId() {
            return id;
        }

        public Users.Role getRole() {
            return role;
        }
    }

