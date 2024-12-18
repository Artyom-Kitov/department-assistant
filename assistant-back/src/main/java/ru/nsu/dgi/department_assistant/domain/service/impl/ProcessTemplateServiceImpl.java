package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessStepDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessRepository;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProcessTemplateServiceImpl implements ProcessTemplateService {

    private final ProcessRepository processRepository;
    private final ProcessGraphService processGraphService;

    @Override
    public ProcessTemplateCreationResponseDto createProcessTemplate(ProcessTemplateCreationRequestDto request) {
        ProcessGraphNode graph = processGraphService.buildFromRequest(request);
    }
}
