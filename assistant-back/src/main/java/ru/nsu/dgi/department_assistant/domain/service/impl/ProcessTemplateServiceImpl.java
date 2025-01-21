package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.mapper.process.ProcessGraphMapper;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessSavingService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessTemplateServiceImpl implements ProcessTemplateService {

    private final ProcessGraphService processGraphService;
    private final ProcessSavingService processSavingService;

    private final ProcessGraphMapper processGraphMapper;

    @Override
    public ProcessTemplateCreationResponseDto createProcessTemplate(ProcessTemplateCreationRequestDto request) {
        ProcessGraphNode root = request.body();
        int duration = processGraphService.calculateDuration(root);
        UUID processId = processSavingService.saveTemplate(request.name(), duration, root);
        return new ProcessTemplateCreationResponseDto(processId);
    }

    @Override
    public ProcessTemplateResponseDto getProcessById(UUID id) {
        ProcessGraph graph = processSavingService.loadTemplate(id);
        return processGraphMapper.toResponse(graph);
    }
}
