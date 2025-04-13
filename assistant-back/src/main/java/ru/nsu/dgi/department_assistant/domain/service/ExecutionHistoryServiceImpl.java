package ru.nsu.dgi.department_assistant.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ExecutionHistoryDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.HistoryCleanupDto;
import ru.nsu.dgi.department_assistant.domain.entity.process.ExecutionHistory;
import ru.nsu.dgi.department_assistant.domain.mapper.process.ExecutionHistoryMapper;
import ru.nsu.dgi.department_assistant.domain.repository.process.ExecutionHistoryRepository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExecutionHistoryServiceImpl implements ExecutionHistoryService {

    private final ExecutionHistoryRepository executionHistoryRepository;
    private final ExecutionHistoryMapper historyMapper;

    private static final Set<String> FIELDS = Arrays.stream(ExecutionHistory.class.getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toUnmodifiableSet());

    @Transactional(readOnly = true)
    @Override
    public List<ExecutionHistoryDto> getHistory(int page, int size, String sortBy, boolean ascending) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = FIELDS.contains(sortBy)
                ? PageRequest.of(page, size, Sort.by(new Sort.Order(direction, sortBy)))
                : PageRequest.of(page, size);
        return executionHistoryRepository.findAll(pageable).stream()
                .map(historyMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void clean(HistoryCleanupDto request) {
        request.historyIds().forEach(executionHistoryRepository::deleteById);
    }
}
