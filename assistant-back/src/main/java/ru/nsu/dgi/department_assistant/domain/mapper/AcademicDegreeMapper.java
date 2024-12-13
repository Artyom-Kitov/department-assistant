package ru.nsu.dgi.department_assistant.domain.mapper;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.AcademicDegreeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.AcademicDegree;

@Mapper(componentModel = "spring")
public interface AcademicDegreeMapper {
    AcademicDegreeResponseDto entityToResponseDto(AcademicDegree academicDegree);
}
