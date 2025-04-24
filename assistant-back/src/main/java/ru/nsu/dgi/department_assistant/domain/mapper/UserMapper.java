package ru.nsu.dgi.department_assistant.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.nsu.dgi.department_assistant.domain.dto.user.UserDto;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "name", source = "name")
    UserDto toDto(Users user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "name", source = "name")
    Users toEntity(UserDto userDto);
} 