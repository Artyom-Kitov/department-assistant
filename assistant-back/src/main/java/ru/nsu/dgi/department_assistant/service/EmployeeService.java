package ru.nsu.dgi.department_assistant.service;

import ru.nsu.dgi.department_assistant.domain.dto.EmployeeRequestDTO;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeResponseDTO;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    public List<EmployeeResponseDTO> getAllEmployees();
    public EmployeeResponseDTO getEmployee(UUID id);
    public void addEmployee(EmployeeRequestDTO employeeRequestDTO);
}
