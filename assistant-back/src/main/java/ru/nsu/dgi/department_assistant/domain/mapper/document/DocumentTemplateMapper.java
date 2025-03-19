package ru.nsu.dgi.department_assistant.domain.mapper.document;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;
@Mapper(componentModel = "spring")
public interface DocumentTemplateMapper {
    DocumentTemplate toEntity(DocumentTemplateDto dto);
    DocumentTemplateDto toDto(DocumentTemplate entity);
}
