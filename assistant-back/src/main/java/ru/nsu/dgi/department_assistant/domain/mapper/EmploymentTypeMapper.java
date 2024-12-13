package ru.nsu.dgi.department_assistant.domain.mapper;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.EmploymentTypeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentType;

@Mapper(componentModel = "spring")
public interface EmploymentTypeMapper {
    EmploymentTypeResponseDto entityToResponseDto(EmploymentType employmentType);
}
