package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateShortDto;

import java.util.List;
import java.util.UUID;

public interface ProcessTemplateService {
    ProcessTemplateCreationResponseDto createProcessTemplate(ProcessTemplateCreationRequestDto request);

    ProcessTemplateResponseDto getProcessById(UUID id);

    int getDurationById(UUID id);

    List<ProcessTemplateShortDto> getAllProcesses();

    void deleteById(UUID id);

    void updateById(UUID id, ProcessTemplateCreationRequestDto request);
}
