package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PostRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PostResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponseDto entityToResponseDto(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employments", ignore = true)
    Post toEntity(PostRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employments", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(PostRequestDto requestDto, @MappingTarget Post post);
}
