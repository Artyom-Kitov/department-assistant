package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.document.DocumentTemplateDto;
import ru.nsu.dgi.department_assistant.domain.entity.document.DocumentTemplate;

import java.util.UUID;

public interface DocumentTemplateService {
    DocumentTemplateDto getTemplateById(UUID id);
    DocumentTemplateDto createTemplate(DocumentTemplateDto templateDto);
    DocumentTemplateDto updateTemplate(UUID id, DocumentTemplateDto templateDto);
    void deleteTemplate(UUID id);

}
