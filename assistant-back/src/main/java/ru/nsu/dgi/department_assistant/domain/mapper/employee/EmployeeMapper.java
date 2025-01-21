package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeResponseDto entityToResponseDto(Employee employee);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    Employee requestToEntity(EmployeeRequestDto employeeRequestDto);
}
