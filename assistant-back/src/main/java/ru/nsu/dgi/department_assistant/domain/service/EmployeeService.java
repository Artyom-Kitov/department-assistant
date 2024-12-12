package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeWithAllInfoResponseDto;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    public List<EmployeeResponseDto> getAllEmployees();
    public EmployeeResponseDto getEmployee(UUID id);
    public void addEmployee(EmployeeRequestDto employeeRequestDto);
    public List<EmployeeWithAllInfoResponseDto> getAllEmployeeWithAllInfos();
    public EmployeeWithAllInfoResponseDto getEmployeeWithAllInfos(UUID id);
}
