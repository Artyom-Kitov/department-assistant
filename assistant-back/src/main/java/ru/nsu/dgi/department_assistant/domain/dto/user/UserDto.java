package ru.nsu.dgi.department_assistant.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;

    private Users.Role role;
    public static UserDto fromUser(Users user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }


} 