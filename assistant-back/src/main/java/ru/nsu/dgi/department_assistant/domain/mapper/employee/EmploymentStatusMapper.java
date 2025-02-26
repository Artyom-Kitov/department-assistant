package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentStatus;

@Mapper(componentModel = "spring")
public interface EmploymentStatusMapper {
    EmploymentStatusResponseDto entityToResponseDto(EmploymentStatus employmentStatus);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    EmploymentStatus toEntity(EmploymentStatusRequestDto employmentStatusRequestDto);
}
