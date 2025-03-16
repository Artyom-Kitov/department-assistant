package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.process.ConditionalExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessCancellationDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessExecutionRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessExecutionStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.StepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.StepStatusDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.SubstepExecutedDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.SubstepsInProcessStatusDto;

import java.util.List;

public interface ProcessExecutionService {
    void startForEmployee(ProcessExecutionRequestDto request);

    void cancel(ProcessCancellationDto request);

    void executeCommonStep(StepExecutedDto dto);

    void executeSubstep(SubstepExecutedDto dto);

    void executeConditional(ConditionalExecutedDto dto);

    List<StepStatusDto> getStatuses(ProcessExecutionStatusRequestDto request);

    List<SubstepsInProcessStatusDto> getSubstepsStatuses(ProcessExecutionStatusRequestDto request);
}
