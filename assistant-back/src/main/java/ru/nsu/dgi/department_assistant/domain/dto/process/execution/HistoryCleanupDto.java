package ru.nsu.dgi.department_assistant.domain.dto.process.execution;

import java.util.List;
import java.util.UUID;

public record HistoryCleanupDto(
        List<UUID> historyIds
) {
}
