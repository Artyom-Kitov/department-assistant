package ru.nsu.dgi.department_assistant.domain.mapper.process;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.process.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;

@Mapper(componentModel = "spring")
public interface ProcessGraphMapper {
    ProcessTemplateResponseDto toResponse(ProcessGraph graph);
}
