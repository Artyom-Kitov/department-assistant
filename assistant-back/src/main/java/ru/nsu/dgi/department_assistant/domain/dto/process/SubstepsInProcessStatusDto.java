package ru.nsu.dgi.department_assistant.domain.dto.process;

import java.util.List;
import java.util.UUID;

public record SubstepsInProcessStatusDto(
        UUID processId,
        int stepId,
        List<SubstepStatusDto> statuses
) {
}
