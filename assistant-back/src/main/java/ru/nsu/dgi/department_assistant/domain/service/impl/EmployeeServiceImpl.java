package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.Employee;
import ru.nsu.dgi.department_assistant.domain.mapper.EmployeeMapper;
import ru.nsu.dgi.department_assistant.domain.repository.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(employeeMapper::entityToResponseDto)
                .toList();
    }

    @Override
    public EmployeeResponseDto getEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        return employeeMapper.entityToResponseDto(employee);
    }

    @Override
    public void addEmployee(EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeMapper.requestToEntity(employeeRequestDto);
        employeeRepository.save(employee);
    }
}
