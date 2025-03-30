package ru.nsu.dgi.department_assistant.domain.mapper.process;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.dgi.department_assistant.domain.dto.process.template.ProcessTemplateResponseDto;
import ru.nsu.dgi.department_assistant.domain.graph.ProcessGraph;

@Mapper(componentModel = "spring")
public interface ProcessGraphMapper {
    @Mapping(target = "steps", expression = "java(graph.nodes().values().stream().toList())")
    ProcessTemplateResponseDto toResponse(ProcessGraph graph);
}
