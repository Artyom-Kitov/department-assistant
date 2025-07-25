package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateCreationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateCreationResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateShortDto;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;
import ru.nsu.dgi.department_assistant.domain.exception.EntityEditException;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.mapper.process.ProcessGraphMapper;
import ru.nsu.dgi.department_assistant.domain.repository.process.EmployeeAtProcessRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessRepository;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessSavingService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessTemplateServiceImpl implements ProcessTemplateService {

    private final ProcessGraphService processGraphService;
    private final ProcessSavingService processSavingService;

    private final ProcessGraphMapper processGraphMapper;

    private final ProcessRepository processRepository;
    private final EmployeeAtProcessRepository employeeAtProcessRepository;

    @Override
    public ProcessTemplateCreationResponseDto createProcessTemplate(ProcessTemplateCreationRequestDto request) {
        log.info("Creating process template with name '{}'", request.name());
        List<ProcessGraphNode> steps = request.steps();
        ProcessGraph graph = processGraphService.buildGraph(UUID.randomUUID(), request.name(), steps);
        processSavingService.saveTemplate(graph);
        log.info("Successfully created and saved template with name '{}' by ID = {}", request.name(), graph.id());
        return new ProcessTemplateCreationResponseDto(graph.id());
    }

    @Override
    public ProcessTemplateResponseDto getProcessById(UUID id) {
        log.info("Getting process template by ID = {}", id);
        ProcessGraph graph = processSavingService.loadTemplate(id);
        log.info("Successfully retrieved process template with ID = {}", id);
        return processGraphMapper.toResponse(graph);
    }

    @Override
    public int getDurationById(UUID id) {
        log.info("Getting process duration by id = {}", id);
        Process process = processRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(id.toString()));
        log.info("Process with id = {} lasts {} days", id, process.getTotalDuration());
        return process.getTotalDuration();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProcessTemplateShortDto> getAllProcesses() {
        log.info("Getting all process template IDs");
        var result = processRepository.findAll().stream()
                .map(p -> new ProcessTemplateShortDto(p.getId(), p.getName(), p.getTotalDuration()))
                .toList();
        log.info("Found {} process templates", result.size());
        return result;
    }

    @Override
    public void deleteById(UUID id) {
        if (employeeAtProcessRepository.existsByProcessId(id)) {
            throw new EntityEditException();
        }
        processRepository.deleteById(id);
    }

    @Override
    public void updateById(UUID id, ProcessTemplateCreationRequestDto request) {
        log.info("Updating process template with id = {}", id);
        deleteById(id);
        List<ProcessGraphNode> steps = request.steps();
        ProcessGraph graph = processGraphService.buildGraph(id, request.name(), steps);
        processSavingService.saveTemplate(graph);
        log.info("Successfully update process template with id = {}", id);
    }
}
