package ru.nsu.dgi.department_assistant.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeRequestDTO;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeResponseDTO;
import ru.nsu.dgi.department_assistant.domain.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeResponseDTO entityToResponseDTO(Employee employee);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    Employee requestToEntity(EmployeeRequestDTO employeeRequestDTO);
}
