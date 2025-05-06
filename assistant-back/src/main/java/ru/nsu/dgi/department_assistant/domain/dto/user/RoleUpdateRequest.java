package ru.nsu.dgi.department_assistant.domain.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;

@Getter
@Setter
@NoArgsConstructor
public class RoleUpdateRequest {
    private Users.Role role;
}