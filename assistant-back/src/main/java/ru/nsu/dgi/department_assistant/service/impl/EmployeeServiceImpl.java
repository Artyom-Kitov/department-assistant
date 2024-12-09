package ru.nsu.dgi.department_assistant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeRequestDTO;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeResponseDTO;
import ru.nsu.dgi.department_assistant.domain.entity.Employee;
import ru.nsu.dgi.department_assistant.domain.mapper.EmployeeMapper;
import ru.nsu.dgi.department_assistant.repository.EmployeeRepository;
import ru.nsu.dgi.department_assistant.service.EmployeeService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(employeeMapper::entityToResponseDTO)
                .toList();
    }

    @Override
    public EmployeeResponseDTO getEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        return employeeMapper.entityToResponseDTO(employee);
    }

    @Override
    public void addEmployee(EmployeeRequestDTO employeeRequestDTO) {
        Employee employee = employeeMapper.requestToEntity(employeeRequestDTO);
        employeeRepository.save(employee);
    }
}
