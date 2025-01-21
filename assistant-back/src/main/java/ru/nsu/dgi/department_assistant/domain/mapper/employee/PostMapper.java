package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PostResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponseDto entityToResponseDto(Post post);
}
