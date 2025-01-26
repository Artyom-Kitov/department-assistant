package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateShortDto;

import java.util.List;
import java.util.UUID;

public interface ProcessTemplateService {
    ProcessTemplateCreationResponseDto createProcessTemplate(ProcessTemplateCreationRequestDto request);

    ProcessTemplateResponseDto getProcessById(UUID id);

    List<ProcessTemplateShortDto> getAllProcesses();

    void deleteById(UUID id);
}
