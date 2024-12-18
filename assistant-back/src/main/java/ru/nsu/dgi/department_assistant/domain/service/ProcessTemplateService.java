package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationResponseDto;

public interface ProcessTemplateService {
    ProcessTemplateCreationResponseDto createProcessTemplate(ProcessTemplateCreationRequestDto request);
}
