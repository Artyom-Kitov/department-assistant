package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;

public interface ProcessGraphService {
    ProcessGraphNode buildFromRequest(ProcessTemplateCreationRequestDto request);

    int calculateDuration(ProcessGraphNode node);

    void saveToDatabase(ProcessGraphNode node);
}
