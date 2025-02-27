package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    List<EmployeeResponseDto> getAllEmployees();

    EmployeeResponseDto getEmployee(UUID id);

    List<EmployeeWithAllInfoResponseDto> getAllEmployeeWithAllInfos();

    EmployeeWithAllInfoResponseDto getEmployeeWithAllInfos(UUID id);

    EmployeeResponseDto createEmployee(EmployeeRequestDto employeeRequestDto);

    EmployeeResponseDto updateEmployee(UUID id, EmployeeRequestDto employeeRequestDto);

    void deleteEmployee(UUID id);
}
