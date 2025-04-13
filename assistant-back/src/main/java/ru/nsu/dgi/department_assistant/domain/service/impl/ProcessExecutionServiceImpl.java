package ru.nsu.dgi.department_assistant.domain.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.config.StepType;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ConditionalExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.EmployeeProcessExecutionDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessCancellationDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessExecutionRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ProcessExecutionStatusDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepCancellationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.SubstepCancellationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.StepStatusDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.SubstepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.SubstepStatusDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateShortDto;
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
import ru.nsu.dgi.department_assistant.domain.repository.process.StepRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.StepStatusRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.SubstepRepository;
import ru.nsu.dgi.department_assistant.domain.repository.process.SubstepStatusRepository;
import ru.nsu.dgi.department_assistant.domain.service.ProcessExecutionService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessGraphService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessSavingService;
import ru.nsu.dgi.department_assistant.domain.service.ProcessTemplateService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessExecutionServiceImpl implements ProcessExecutionService {

    private final ProcessTemplateService processTemplateService;
    private final StepRepository stepRepository;
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
                            .sorted(Comparator.comparing(StepStatusDto::deadline))
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

    private List<SubstepStatus> getOtherSubstepStatuses(SubstepStatus substepStatus) {
        Step originalStep = substepStatus.getSubstep().getStep();
        List<Substep> allSubsteps = substepRepository.findAllByStep(originalStep);
        return allSubsteps.stream()
                .map(substep ->  {
                    SubstepStatusId statusId = new SubstepStatusId(substepStatus.getEmployeeId(),
                            substepStatus.getStartProcessId(), substep.getId());
                    return substepStatusRepository.findById(statusId)
                            .orElseThrow(InvalidStepExecutionException::new);
                })
                .toList();
    }

    private boolean otherSubstepsAreCompleted(SubstepStatus substepStatus) {
        return getOtherSubstepStatuses(substepStatus).stream().allMatch(SubstepStatus::isCompleted);
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
                .filter(s -> s.getCompletedAt() == null || s.getIsSuccessful() != null
                        && !s.getFullId().equals(current.getFullId()))
                .toList();

        return new EmployeeProcessExecutionDto(
                completed.stream().map(this::mapToDto).toList(),
                mapToDto(current),
                toComplete.stream().map(this::mapToDto).toList()
        );
    }

    @Override
    public List<ProcessTemplateShortDto> getByEmployee(UUID employeeId) {
        return employeeAtProcessRepository.findAllByEmployee(employeeId).stream()
                .map(eap -> {
                    Process p = processRepository.findById(eap.getProcessId()).orElseThrow();
                    return new ProcessTemplateShortDto(p.getId(), p.getName(), p.getTotalDuration());
                })
                .toList();
    }

    @Transactional
    @Override
    public void cancelStep(StepCancellationRequestDto request) {
        StepStatus status = stepStatusRepository.findById(new StepStatusId(
                request.employeeId(), request.startProcessId(), request.processId(), request.stepId()))
                .orElseThrow(() -> new EntityNotFoundException(request.toString()));
        if (status.getCompletedAt() == null) {
            throw new InvalidStepExecutionException();
        }
        StepType type = StepType.of(status.getStep().getType());
        EmployeeAtProcess employeeAtProcess = status.getEmployeeAtProcess();
        StepStatus currentStepStatus = stepStatusRepository.findById(
                new StepStatusId(request.employeeId(), request.startProcessId(),
                        employeeAtProcess.getCurrentStep().getProcessId(), employeeAtProcess.getCurrentStep().getId())
        ).orElseThrow(InvalidStepExecutionException::new);

        if (!getNextSteps(status.getProcessId(), status.getStepId()).contains(currentStepStatus.getStep().getStepId())) {
            throw new InvalidStepExecutionException();
        }
        switch (type) {
            case COMMON -> cancelCommon(status);
            case CONDITIONAL -> cancelConditional(status);
            default -> throw new InvalidStepExecutionException();
        }
        updateCurrentStep(employeeAtProcess, status.getStep().getId(), status.getStep().getProcessId());
    }

    private Set<StepId> getNextSteps(UUID processId, int stepId) {
        var transitionId = new TransitionId(stepId, processId);
        Set<StepId> result = new HashSet<>();
        if (commonTransitionRepository.existsById(transitionId)) {
            int common = commonTransitionRepository.findById(transitionId)
                    .orElseThrow(() -> new EntityNotFoundException(processId + " " + stepId)).getNextStepId();
            result.add(new StepId(common, processId));
        }
        if (conditionalTransitionRepository.existsById(transitionId)) {
            ConditionalTransition conditional = conditionalTransitionRepository.findById(transitionId)
                    .orElseThrow(() -> new EntityNotFoundException(processId + " " + stepId));
            result.add(new StepId(conditional.getPositiveStepId(), processId));
            result.add(new StepId(conditional.getNegativeStepId(), processId));
        }
        return result;
    }

    private void cancelCommon(StepStatus status) {
        status.setCompletedAt(null);
        stepStatusRepository.save(status);
    }

    @Transactional
    @Override
    public void cancelSubstep(SubstepCancellationRequestDto request) {
        SubstepStatus status = substepStatusRepository.findById(
                new SubstepStatusId(request.employeeId(), request.startProcessId(), request.startProcessId()))
                .orElseThrow(() -> new EntityNotFoundException(request.toString()));
        status.setCompleted(false);
        substepStatusRepository.save(status);
        Step holder = status.getSubstep().getStep();
        StepStatus holderStatus = stepStatusRepository.findById(
                new StepStatusId(request.employeeId(), request.startProcessId(), holder.getProcessId(), holder.getId()))
                .orElseThrow(() -> new EntityNotFoundException(request.toString()));
        if (!holderStatus.getEmployeeAtProcess().getCurrentStep().getStepId().equals(holderStatus.getStep().getStepId()) &&
                !getNextSteps(holderStatus.getProcessId(), holderStatus.getStepId()).contains(
                holderStatus.getEmployeeAtProcess().getCurrentStep().getStepId())) {
            throw new InvalidStepExecutionException();
        }
        if (holderStatus.getCompletedAt() != null) {
            cancelCommon(holderStatus);
            updateCurrentStep(holderStatus.getEmployeeAtProcess(), holderStatus.getStepId(), holderStatus.getProcessId());
        }
        status.setCompleted(false);
        substepStatusRepository.save(status);
    }

    private void cancelConditional(StepStatus status) {
        status.setCompletedAt(null);
        Boolean branch = status.getIsSuccessful();
        if (branch == null) {
            throw new InvalidStepExecutionException();
        }
        ConditionalTransition transition = conditionalTransitionRepository.findById(new TransitionId(status.getStepId(), status.getProcessId()))
                .orElseThrow();
        int next = branch ? transition.getPositiveStepId() : transition.getNegativeStepId();
        StepStatus current = stepStatusRepository.findById(new StepStatusId(status.getEmployeeId(), status.getStartProcessId(),
                status.getProcessId(), next)).orElse(null);
        while (current != null) {
            if (current.getStep().getType() == StepType.SUBTASKS.getValue()) {
                cancelCommon(current);
                getSubstepsStatuses(current).forEach(s -> {
                    SubstepStatusId substepStatusId = new SubstepStatusId(status.getEmployeeId(), status.getStartProcessId(),
                            s.substepId());
                    SubstepStatus substepStatus = substepStatusRepository.findById(substepStatusId).orElseThrow();
                    substepStatus.setCompleted(false);
                    substepStatusRepository.save(substepStatus);
                });
            }
            stepStatusRepository.deleteById(current.getFullId());
            CommonTransition t = commonTransitionRepository.findById(new TransitionId(current.getStepId(), current.getProcessId()))
                    .orElse(null);
            if (t == null) {
                current = null;
            } else {
                current = stepStatusRepository.findById(new StepStatusId(current.getEmployeeId(),
                        current.getStartProcessId(), current.getProcessId(), t.getNextStepId()))
                        .orElse(null);
            }
        }
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
                status.getStep().getType(),
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
                .map(s -> new SubstepStatusDto(s.getSubstepId(), s.isCompleted()))
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

    private void markAsStartedImpl(Employee employee, UUID startProcessId, UUID processId, ProcessGraph graph,
                                   int nodeId, @Nullable LocalDate startDate) {
        ProcessGraphNode node = graph.getNode(nodeId);
        LocalDate endDate = startDate != null ? startDate.plusDays(node.getDuration()) : null;

        StepStatus stepStatus = new StepStatus(employee.getId(), processId, node.getId(), startProcessId, endDate, null, null);
        EmployeeAtProcess employeeAtProcess = employeeAtProcessRepository.findById(new EmployeeAtProcessId(employee.getId(),
                startProcessId)).orElseThrow();
        stepStatus.setEmployeeAtProcess(employeeAtProcess);
        Step step = stepRepository.findById(new StepId(nodeId, processId)).orElseThrow();
        stepStatus.setStep(step);
        if (node.getData() instanceof StartStepData) {
            stepStatus.setCompletedAt(LocalDate.now());
        }
        stepStatusRepository.save(stepStatus);
        if (node.getData() instanceof SubtasksStepData d) {
            d.getSubtasks().forEach(task -> {
                SubstepStatus substepStatus = new SubstepStatus(employee.getId(), startProcessId, task.id(),
                        false, null, null);
                substepStatusRepository.save(substepStatus);
            });
        }
        // We don't calculate deadlines if we don't know yet the way we'll actually go
        if (node.next().size() <= 1) {
            node.next().forEach(nextId -> markAsStartedImpl(employee, startProcessId, processId, graph, nextId, endDate));
        }
    }
}
