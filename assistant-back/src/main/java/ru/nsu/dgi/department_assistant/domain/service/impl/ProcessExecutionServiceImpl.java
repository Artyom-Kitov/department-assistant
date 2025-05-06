package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.config.StepType;
import ru.nsu.dgi.department_assistant.domain.dto.documents.DocumentTypeDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ConditionalExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.EmployeeProcessExecutionDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessCancellationDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessExecutionRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessExecutionStatusDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepStatusDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.SubstepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.SubstepStatusDto;
import ru.nsu.dgi.department_assistant.domain.entity.documents.DocumentType;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.process.CommonTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.ConditionalTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.EmployeeAtProcess;
import ru.nsu.dgi.department_assistant.domain.entity.process.ExecutionHistory;
import ru.nsu.dgi.department_assistant.domain.entity.process.Process;
import ru.nsu.dgi.department_assistant.domain.entity.process.Step;
import ru.nsu.dgi.department_assistant.domain.entity.process.StepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.Substep;
import ru.nsu.dgi.department_assistant.domain.entity.process.SubstepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.EmployeeAtProcessId;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepId;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepStatusId;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.SubstepStatusId;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.TransitionId;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidStepExecutionException;
import ru.nsu.dgi.department_assistant.domain.exception.ProcessExecutionStartException;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraphNode;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.CommonStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.StartStepData;
import ru.nsu.dgi.department_assistant.domain.graph.stepdata.SubtasksStepData;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.CommonTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ConditionalTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.EmployeeAtProcessRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ExecutionHistoryRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.FinalTypeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.ProcessTransitionRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.StepStatusRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.SubstepRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.SubstepStatusRepository;
import ru.nsu.dgi.department_assistant.domain.service.ProcessExecutionService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessSavingService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessExecutionServiceImpl implements ProcessExecutionService {

    private final ProcessTemplateService processTemplateService;
    private final ProcessGraphService processGraphService;
    private final ProcessSavingService processSavingService;

    private final EmployeeAtProcessRepository employeeAtProcessRepository;
    private final StepStatusRepository stepStatusRepository;
    private final SubstepStatusRepository substepStatusRepository;
    private final EmployeeRepository employeeRepository;
    private final ProcessRepository processRepository;
    private final CommonTransitionRepository commonTransitionRepository;
    private final ConditionalTransitionRepository conditionalTransitionRepository;
    private final FinalTypeRepository finalTypeRepository;
    private final SubstepRepository substepRepository;
    private final ProcessTransitionRepository processTransitionRepository;

    private final ExecutionHistoryRepository executionHistoryRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ProcessExecutionStatusDto> getProcessStatuses() {
        record IdAndName(UUID processId, String name) {}
        return employeeAtProcessRepository.findAll().stream()
                .collect(Collectors.groupingBy(e -> {
                    Process process = processRepository.findById(e.getProcessId())
                            .orElseThrow();
                    return new IdAndName(process.getId(), process.getName());
                }))
                .entrySet().stream()
                .map(e -> {
                    List<StepStatusDto> statuses = e.getValue().stream()
                            .map(s -> {
                                var stepStatusId = new StepStatusId(s.getEmployeeId(), s.getProcessId(),
                                        s.getCurrentStepProcessId(), s.getCurrentStepId());
                                return mapToDto(stepStatusRepository.findById(stepStatusId).orElseThrow());
                            })
                            .toList();
                    return new ProcessExecutionStatusDto(e.getKey().processId(), e.getKey().name(), statuses);
                })
                .toList();
    }

    @Transactional
    @Override
    public void startForEmployee(ProcessExecutionRequestDto request) {
        LocalDate deadline = request.deadline();
        EmployeeAtProcessId id = new EmployeeAtProcessId(request.employeeId(), request.processId());
        if (employeeAtProcessRepository.existsById(id)) {
            throw new ProcessExecutionStartException(request.employeeId(), request.processId());
        }
        ProcessTemplateResponseDto process = processTemplateService.getProcessById(request.processId());
        ProcessGraph graph = processGraphService.buildGraph(process.id(), process.name(), process.steps());

        int current = ((StartStepData) graph.getNode(graph.start()).getData()).getNext();
        employeeAtProcessRepository.save(new EmployeeAtProcess(request.employeeId(), request.processId(), null,
                LocalDate.now(), deadline, request.processId(), current, null));
        markAsStarted(request.employeeId(), request.processId(), request.processId(), graph.start(),
                graph, deadline);
    }

    @Transactional
    @Override
    public void cancel(ProcessCancellationDto request) {
        EmployeeAtProcessId employeeAtProcessId = new EmployeeAtProcessId(request.employeeId(),
                request.processId());
        employeeAtProcessRepository.deleteById(employeeAtProcessId);
    }

    @Transactional
    @Override
    public void executeCommonStep(StepExecutedDto dto) {
        var employeeAtProcessId = new EmployeeAtProcessId(dto.employeeId(), dto.startProcessId());
        if (!employeeAtProcessRepository.existsById(employeeAtProcessId)) {
            throw new InvalidStepExecutionException();
        }
        var stepStatusId = new StepStatusId(dto.employeeId(), dto.startProcessId(), dto.processId(), dto.stepId());
        StepStatus stepStatus = stepStatusRepository.findById(stepStatusId).orElseThrow(
                InvalidStepExecutionException::new
        );
        if (stepStatus.getStep().getType() != StepType.COMMON.getValue()) {
            throw new InvalidStepExecutionException();
        }
        checkIfPossibleToComplete(stepStatus, employeeAtProcessId);

        LocalDate completedAt = LocalDate.now();
        stepStatus.setCompletedAt(completedAt);
        stepStatus.setIsSuccessful(true);
        stepStatusRepository.save(stepStatus);
        postProcess(stepStatus);
    }

    @Transactional
    @Override
    public void executeSubstep(SubstepExecutedDto dto) {
        var employeeAtProcessId = new EmployeeAtProcessId(dto.employeeId(), dto.startProcessId());
        if (!employeeAtProcessRepository.existsById(employeeAtProcessId)) {
            throw new InvalidStepExecutionException();
        }

        SubstepStatusId substepStatusId = new SubstepStatusId(dto.employeeId(), dto.startProcessId(), dto.substepId());
        SubstepStatus substepStatus = substepStatusRepository.findById(substepStatusId)
                .orElseThrow(InvalidStepExecutionException::new);
        if (substepStatus.isCompleted()) {
            throw new InvalidStepExecutionException();
        }
        Step step = substepStatus.getSubstep().getStep();
        StepStatusId stepStatusId = new StepStatusId(dto.employeeId(), dto.startProcessId(),
                step.getProcessId(), step.getId());
        StepStatus stepStatus = stepStatusRepository.findById(stepStatusId)
                .orElseThrow(InvalidStepExecutionException::new);
        checkIfPossibleToComplete(stepStatus, employeeAtProcessId);

        substepStatus.setCompleted(true);
        substepStatusRepository.save(substepStatus);
        if (otherSubstepsAreCompleted(substepStatus)) {
            stepStatus.setCompletedAt(LocalDate.now());
            stepStatus.setIsSuccessful(true);
            stepStatusRepository.save(stepStatus);
            postProcess(stepStatus);
        }
    }

    @Transactional
    @Override
    public void executeConditional(ConditionalExecutedDto dto) {
        var employeeAtProcessId = new EmployeeAtProcessId(dto.employeeId(), dto.startProcessId());
        EmployeeAtProcess employeeAtProcess = employeeAtProcessRepository.findById(employeeAtProcessId)
                .orElseThrow(InvalidStepExecutionException::new);
        var stepStatusId = new StepStatusId(dto.employeeId(), dto.startProcessId(), dto.processId(), dto.stepId());
        StepStatus stepStatus = stepStatusRepository.findById(stepStatusId).orElseThrow(
                InvalidStepExecutionException::new
        );
        if (stepStatus.getStep().getType() != StepType.CONDITIONAL.getValue()) {
            throw new InvalidStepExecutionException();
        }
        checkIfPossibleToComplete(stepStatus, employeeAtProcessId);
        stepStatus.setCompletedAt(LocalDate.now());
        stepStatus.setIsSuccessful(dto.successful());

        var transitionId = new TransitionId(stepStatus.getStepId(), stepStatus.getProcessId());
        ConditionalTransition transition = conditionalTransitionRepository.findById(transitionId)
                .orElseThrow(() -> new EntityNotFoundException(transitionId.toString()));

        int nextStepId = dto.successful() ? transition.getPositiveStepId() : transition.getNegativeStepId();
        ProcessGraph graph = processSavingService.loadTemplate(dto.processId());
        markAsStarted(dto.employeeId(), dto.startProcessId(), dto.processId(), nextStepId, graph,
                employeeAtProcess.getDeadline());
        postProcess(stepStatus, nextStepId);
    }

    private void updateCurrentStep(EmployeeAtProcess employeeAtProcess, int stepId, UUID processId) {
        employeeAtProcess.setCurrentStepId(stepId);
        employeeAtProcess.setCurrentStepProcessId(processId);
        employeeAtProcessRepository.save(employeeAtProcess);
    }

    private boolean otherSubstepsAreCompleted(SubstepStatus substepStatus) {
        Step originalStep = substepStatus.getSubstep().getStep();
        List<Substep> allSubsteps = substepRepository.findAllByStep(originalStep);
        return allSubsteps.stream().allMatch(substep -> {
            SubstepStatusId statusId = new SubstepStatusId(substepStatus.getEmployeeId(),
                    substepStatus.getStartProcessId(), substep.getId());
            SubstepStatus status = substepStatusRepository.findById(statusId)
                    .orElseThrow(InvalidStepExecutionException::new);
            return status.isCompleted();
        });
    }

    @Transactional(readOnly = true)
    @Override
    public EmployeeProcessExecutionDto getStatuses(UUID employeeId, UUID processId) {
        var employeeAtProcessId = new EmployeeAtProcessId(employeeId, processId);
        if (!employeeAtProcessRepository.existsById(employeeAtProcessId)) {
            throw new EntityNotFoundException(employeeAtProcessId.toString());
        }
        List<StepStatus> statuses = stepStatusRepository.findByEmployeeAndStartProcess(employeeId, processId);
        List<StepStatus> completed = statuses.stream()
                .filter(status -> status.getCompletedAt() != null)
                .toList();

        EmployeeAtProcess employeeAtProcess = employeeAtProcessRepository
                .findById(new EmployeeAtProcessId(employeeId, processId))
                .orElseThrow();
        StepStatus current = stepStatusRepository.findById(new StepStatusId(employeeId, processId,
                employeeAtProcess.getCurrentStepProcessId(), employeeAtProcess.getCurrentStepId()))
                .orElseThrow();
        List<StepStatus> toComplete = statuses.stream()
                .filter(s -> s.getCompletedAt() == null && !s.getFullId().equals(current.getFullId()))
                .toList();

        return new EmployeeProcessExecutionDto(
                completed.stream().map(this::mapToDto).toList(),
                mapToDto(current),
                toComplete.stream().map(this::mapToDto).toList()
        );
    }

    private StepStatusDto mapToDto(StepStatus status) {
        List<SubstepStatusDto> substepStatuses = null;
        if (status.getStep().getType() == StepType.SUBTASKS.getValue()) {
            substepStatuses = getSubstepsStatuses(status);
        }
        return new StepStatusDto(
                status.getEmployeeId(),
                status.getProcessId(),
                status.getStepId(),
                status.getStartProcessId(),
                status.getDeadline(),
                status.getCompletedAt(),
                status.getIsSuccessful(),
                substepStatuses
        );
    }

    private List<SubstepStatusDto> getSubstepsStatuses(StepStatus stepStatus) {
        return substepRepository.findAllByStep(stepStatus.getStep()).stream()
                .map(s -> {
                    SubstepStatusId statusId = new SubstepStatusId(stepStatus.getEmployeeId(), stepStatus.getStartProcessId(),
                            s.getId());
                    return substepStatusRepository.findById(statusId).orElseThrow();
                })
                .map(s -> new SubstepStatusDto(
                    s.getSubstepId(), 
                    s.isCompleted(), 
                    s.getSubstep().getDocumentType() != null ? 
                        DocumentTypeDto.fromEntity(s.getSubstep().getDocumentType()) :
                        null
                ))
                .toList();
    }

    private void postProcess(StepStatus stepStatus) {
        TransitionId transitionId = new TransitionId(stepStatus.getStepId(), stepStatus.getProcessId());
        CommonTransition transition = commonTransitionRepository.findById(transitionId).orElseThrow();
        postProcess(stepStatus, transition.getNextStepId());
    }

    private void postProcess(StepStatus stepStatus, int nextStepId) {
        var employeeAtProcess = employeeAtProcessRepository
                .findById(new EmployeeAtProcessId(stepStatus.getEmployeeId(), stepStatus.getProcessId()))
                .orElseThrow();
        updateCurrentStep(employeeAtProcess, nextStepId, stepStatus.getProcessId());

        StepStatusId nextStepStatusId = new StepStatusId(stepStatus.getEmployeeId(), stepStatus.getStartProcessId(),
                stepStatus.getProcessId(), nextStepId);
        StepStatus nextStepStatus = stepStatusRepository.findById(nextStepStatusId).orElseThrow();
        if (nextStepStatus.getStep().getType() == StepType.FINAL.getValue()) {
            var finalType = finalTypeRepository.findById(new TransitionId(nextStepStatus.getStepId(),
                    nextStepStatus.getProcessId())).orElseThrow();
            completeFinal(nextStepStatus, finalType.isSuccessful());
        } else if (nextStepStatus.getStep().getType() == StepType.TRANSITION.getValue()) {
            var processTransition = processTransitionRepository.findById(new TransitionId(nextStepStatus.getStepId(),
                    nextStepStatus.getProcessId())).orElseThrow();
            completeTransition(nextStepStatus, processTransition.getNextProcessId());
        }
    }

    private void completeFinal(StepStatus stepStatus, boolean isSuccessful) {
        LocalDate completedAt = LocalDate.now();
        EmployeeAtProcessId employeeAtProcessId = new EmployeeAtProcessId(stepStatus.getEmployeeId(),
                stepStatus.getStartProcessId());
        EmployeeAtProcess employeeAtProcess = employeeAtProcessRepository.findById(employeeAtProcessId).orElseThrow(
                InvalidStepExecutionException::new);
        LocalDate startedAt = employeeAtProcess.getStartedAt();
        ExecutionHistory history = new ExecutionHistory(UUID.randomUUID(), stepStatus.getEmployeeId(),
                stepStatus.getStartProcessId(), startedAt, completedAt, isSuccessful, null, null);
        employeeAtProcessRepository.delete(employeeAtProcess);
        executionHistoryRepository.save(history);
    }

    private void completeTransition(StepStatus stepStatus, UUID nextProcessId) {
        stepStatus.setCompletedAt(LocalDate.now());
        stepStatusRepository.save(stepStatus);
        ProcessTemplateResponseDto nextProcess = processTemplateService.getProcessById(nextProcessId);
        var graph = processGraphService.buildGraph(nextProcessId, nextProcess.name(), nextProcess.steps());

        int current = ((CommonStepData) graph.getNode(graph.start()).getData()).getNext();
        var employeeAtProcess = stepStatus.getEmployeeAtProcess();
        employeeAtProcess.setCurrentStepProcessId(nextProcessId);
        employeeAtProcess.setCurrentStepId(current);
        employeeAtProcessRepository.save(employeeAtProcess);

        markAsStarted(stepStatus.getEmployeeId(), stepStatus.getStartProcessId(), nextProcessId, graph.start(),
                graph, stepStatus.getEmployeeAtProcess().getDeadline());
    }

    private void checkIfPossibleToComplete(StepStatus stepStatus, EmployeeAtProcessId employeeAtProcessId) {
        EmployeeAtProcess employeeAtProcess = employeeAtProcessRepository.findById(employeeAtProcessId).orElseThrow();
        StepId current = employeeAtProcess.getCurrentStep().getStepId();
        if (!stepStatus.getStep().getStepId().equals(current)) {
            throw new InvalidStepExecutionException();
        }
        if (stepStatus.getCompletedAt() != null) {
            throw new InvalidStepExecutionException();
        }
    }

    private void markAsStarted(UUID employeeId, UUID startProcessId, UUID processId, int stepId, ProcessGraph graph,
                               @Nullable LocalDate deadline) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(employeeId.toString()));
        int duration = processGraphService.calculateDurationStartingFrom(graph, stepId);
        LocalDate startDate = deadline != null ? deadline.minusDays(duration) : null;
        markAsStartedImpl(employee, startProcessId, processId, graph, stepId, startDate);
    }

    private void markAsStartedImpl(Employee employee, UUID startProcessId, UUID processId, ProcessGraph graph, int nodeId,
                                 @Nullable LocalDate startDate) {
        ProcessGraphNode node = graph.getNode(nodeId);
        LocalDate endDate = startDate != null ? startDate.plusDays(node.getDuration()) : null;

        StepStatus stepStatus = new StepStatus(employee.getId(), processId, node.getId(), startProcessId, endDate, null, null);
        if (node.getData() instanceof StartStepData) {
            stepStatus.setCompletedAt(LocalDate.now());
        }
        stepStatusRepository.save(stepStatus);
        if (node.getData() instanceof SubtasksStepData d) {
            d.getSubtasks().forEach(task -> {
                SubstepStatus substepStatus = new SubstepStatus();
                substepStatus.setEmployeeId(employee.getId());
                substepStatus.setStartProcessId(startProcessId);
                substepStatus.setSubstepId(task.id());
                substepStatus.setCompleted(false);
                
                DocumentType docType = task.documentType();
                if (docType != null) {
                    substepStatus.setDocumentType(docType);
                }
                substepStatusRepository.save(substepStatus);
            });
        }
        // We don't calculate deadlines if we don't know yet the way we'll actually go
        if (node.next().size() <= 1) {
            node.next().forEach(nextId -> markAsStartedImpl(employee, startProcessId, processId, graph, nextId, endDate));
        }
    }
}
