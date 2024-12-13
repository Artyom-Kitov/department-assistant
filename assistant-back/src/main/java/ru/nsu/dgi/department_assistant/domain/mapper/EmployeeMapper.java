package ru.nsu.dgi.department_assistant.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeResponseDto entityToResponseDto(Employee employee);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    Employee requestToEntity(EmployeeRequestDto employeeRequestDto);
}
