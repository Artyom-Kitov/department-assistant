package ru.nsu.dgi.department_assistant.domain.mapper.documents;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.documents.FileResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.documents.FileEntity;

@Mapper(componentModel = "spring")
public interface FileEntityMapper {
    FileResponseDto toDto(FileEntity entity);
}
