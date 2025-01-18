package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessSavingService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessTemplateServiceImpl implements ProcessTemplateService {

    private final ProcessGraphService processGraphService;
    private final ProcessSavingService processSavingService;

    @Override
    public ProcessTemplateCreationResponseDto createProcessTemplate(ProcessTemplateCreationRequestDto request) {
        ProcessGraphNode root = processGraphService.buildFromRequest(request);
        UUID processId = processSavingService.saveTemplateToDb(request.name(), root.getDuration(), root);
        return new ProcessTemplateCreationResponseDto(processId);
    }
}
