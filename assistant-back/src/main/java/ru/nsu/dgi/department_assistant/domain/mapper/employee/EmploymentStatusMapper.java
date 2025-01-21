package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentStatus;

@Mapper(componentModel = "spring")
public interface EmploymentStatusMapper {
    EmploymentStatusResponseDto entityToResponseDto(EmploymentStatus employmentStatus);
}
