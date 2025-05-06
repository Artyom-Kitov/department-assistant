package ru.nsu.dgi.department_assistant.domain.mapper.process;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.process.execution.ExecutionHistoryDto;
import ru.nsu.dgi.department_assistant.domain.entity.process.ExecutionHistory;

@Mapper(componentModel = "spring")
public interface ExecutionHistoryMapper {
    ExecutionHistoryDto toDto(ExecutionHistory executionHistory);
}
