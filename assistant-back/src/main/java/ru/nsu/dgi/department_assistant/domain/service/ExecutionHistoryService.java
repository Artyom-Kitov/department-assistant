package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ExecutionHistoryDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.HistoryCleanupDto;

import java.util.List;

public interface ExecutionHistoryService {
    List<ExecutionHistoryDto> getHistory(int page, int size, String sortBy, boolean ascending);

    void clean(HistoryCleanupDto request);
}
