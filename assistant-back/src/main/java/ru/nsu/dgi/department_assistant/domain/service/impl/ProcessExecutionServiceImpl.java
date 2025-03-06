package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.config.StepType;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessExecutionRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessExecutionStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.StepStatusDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.process.CommonTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.EmployeeAtProcess;
import ru.nsu.dgi.department_assistant.domain.entity.process.StepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.SubstepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.EmployeeAtProcessId;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepStatusId;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.TransitionId;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidStepExecutionException;
import ru.nsu.dgi.department_assistant.domain.exception.ProcessExecutionStartException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.CommonTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ConditionalTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.EmployeeAtProcessRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.StepStatusRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.SubstepStatusRepository;
import ru.nsu.dgi.department_assistant.domain.service.ProcessExecutionService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessExecutionServiceImpl implements ProcessExecutionService {

    private final ProcessTemplateService processTemplateService;
    private final ProcessGraphService processGraphService;

    private final EmployeeAtProcessRepository employeeAtProcessRepository;
    private final StepStatusRepository stepStatusRepository;
    private final SubstepStatusRepository substepStatusRepository;
    private final EmployeeRepository employeeRepository;
    private final CommonTransitionRepository commonTransitionRepository;
    private final ConditionalTransitionRepository conditionalTransitionRepository;

    @Transactional
    @Override
    public void startForEmployee(ProcessExecutionRequestDto request) {
        EmployeeAtProcessId id = new EmployeeAtProcessId(request.employeeId(), request.processId());
        if (employeeAtProcessRepository.existsById(id)) {
            throw new ProcessExecutionStartException(request.employeeId(), request.processId());
        }
        employeeAtProcessRepository.save(new EmployeeAtProcess(request.employeeId(), request.processId(), null, LocalDate.now()));
        ProcessTemplateResponseDto process = processTemplateService.getProcessById(request.processId());
        ProcessGraph graph = processGraphService.buildGraph(process.id(), process.name(), process.steps());
        markAsStarted(request.employeeId(), request.processId(), graph, request.deadline());
    }

    @Transactional
    @Override
    public void executeCommonStep(UUID employeeId, StepExecutedDto dto) {
        var employeeAtProcessId = new EmployeeAtProcessId(dto.employeeId(), dto.processId());
        if (!employeeAtProcessRepository.existsById(employeeAtProcessId)) {
            throw new InvalidStepExecutionException(dto.stepId(), dto.processId());
        }
        StepStatusId stepStatusId = new StepStatusId(employeeId, dto.processId(), dto.stepId());
        StepStatus stepStatus = stepStatusRepository.findById(stepStatusId).orElseThrow();
        if (stepStatus.getStep().getType() != StepType.COMMON.getValue()) {
            throw new InvalidStepExecutionException(dto.stepId(), dto.processId());
        }
        if (!possibleToComplete(stepStatus)) {
            throw new InvalidStepExecutionException(dto.stepId(), dto.processId());
        }
        LocalDate completedAt = LocalDate.now();
        stepStatus.setCompletedAt(completedAt);
        stepStatus.setIsSuccessful(true);
        stepStatusRepository.save(stepStatus);
        completeNextIfFinalOrTransition(stepStatus);
    }

    @Override
    public List<StepStatusDto> getStatuses(ProcessExecutionStatusRequestDto request) {
        var employeeAtProcessId = new EmployeeAtProcessId(request.employeeId(), request.processId());
        if (!employeeAtProcessRepository.existsById(employeeAtProcessId)) {
            throw new EntityNotFoundException(employeeAtProcessId.toString());
        }
        return stepStatusRepository.findByEmployeeAndStartProcess(request.employeeId(), request.processId()).stream()
                .map(status -> new StepStatusDto(
                        status.getStepId(),
                        status.getStartProcessId(),
                        status.getDeadline(),
                        status.getCompletedAt(),
                        status.getIsSuccessful()
                ))
                .collect(Collectors.toList());
    }

    private void completeNextIfFinalOrTransition(StepStatus stepStatus) {
        TransitionId transitionId = new TransitionId(stepStatus.getStepId(), stepStatus.getProcessId());
        CommonTransition transition = commonTransitionRepository.findById(transitionId).orElseThrow();
        StepStatusId nextStepStatusId = new StepStatusId(stepStatus.getEmployeeId(), stepStatus.getProcessId(),
                transition.getNextStepId());
        StepStatus nextStepStatus = stepStatusRepository.findById(nextStepStatusId).orElseThrow();
        if (nextStepStatus.getStep().getType() == StepType.FINAL.getValue()) {
            completeFinal(nextStepStatus);
        } else if (nextStepStatus.getStep().getType() == StepType.TRANSITION.getValue()) {
            completeTransition(nextStepStatus);
        }
    }

    private void completeFinal(StepStatus stepStatus) {
        stepStatus.setCompletedAt(LocalDate.now());
        stepStatusRepository.save(stepStatus);
        // TODO: cleanup + history
    }

    private void completeTransition(StepStatus stepStatus) {
        stepStatus.setCompletedAt(LocalDate.now());
        stepStatusRepository.save(stepStatus);
        // TODO: add subprocess steps
    }

    private boolean possibleToComplete(StepStatus stepStatus) {
        if (stepStatus.getCompletedAt() != null) {
            return false;
        }
        long completedPrevCommons = commonTransitionRepository.findByNextInProcess(stepStatus.getProcessId(),
                stepStatus.getStepId()).stream()
                .map(transition -> {
                    StepStatusId stepStatusId = new StepStatusId(stepStatus.getEmployeeId(), stepStatus.getProcessId(),
                            transition.getStepId());
                    return stepStatusRepository.findById(stepStatusId);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(status -> status.getCompletedAt() != null)
                .count();

        long completedPrevConditionals = conditionalTransitionRepository.findByNextInProcess(stepStatus.getProcessId(),
                stepStatus.getStepId()).stream()
                .map(transition -> {
                    StepStatusId stepStatusId = new StepStatusId(stepStatus.getEmployeeId(), stepStatus.getProcessId(),
                            transition.getStepId());
                    return stepStatusRepository.findById(stepStatusId);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(status -> status.getCompletedAt() != null)
                .count();

        return completedPrevCommons != 0 || completedPrevConditionals != 0;
    }

    private void markAsStarted(UUID employeeId, UUID processId, ProcessGraph graph, LocalDate deadline) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(employeeId.toString()));
        LocalDate startDate = deadline != null ? deadline.minusDays(graph.duration()) : null;
        markAsStartedImpl(employee, processId, graph, graph.start(), startDate);
    }

    private void markAsStartedImpl(Employee employee, UUID processId, ProcessGraph graph, int nodeId, LocalDate startDate) {
        ProcessGraphNode node = graph.getNode(nodeId);
        LocalDate endDate = startDate != null ? startDate.plusDays(node.getDuration()) : null;

        StepStatus stepStatus = new StepStatus(employee.getId(), processId, node.getId(), processId, endDate, null, null);
        stepStatusRepository.save(stepStatus);
        if (node.getData() instanceof SubtasksStepData d) {
            d.getSubtasks().forEach(task -> {
                SubstepStatus substepStatus = new SubstepStatus(employee.getId(), task.id(),
                        false, null, null);
                substepStatusRepository.save(substepStatus);
            });
        }
        node.next().forEach(nextId -> markAsStartedImpl(employee, processId, graph, nextId, endDate));
    }
}
