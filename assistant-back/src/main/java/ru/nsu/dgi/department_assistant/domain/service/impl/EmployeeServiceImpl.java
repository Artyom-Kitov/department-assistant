package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.EmployeeMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
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
    public List<EmployeeWithAllInfoResponseDto> getAllEmployeeWithAllInfos() {
        List<Employee> employees = employeeRepository.findAllEmployeesWithInfo();

        return employees.stream()
                .map(employeeMapper::entityToWithInfoResponse)
                .toList();
    }

    @Override
    public EmployeeWithAllInfoResponseDto getEmployeeWithAllInfos(UUID id) {
        Employee employee = employeeRepository.findEmployeeWithInfoById(id).orElse(null);

        return employeeMapper.entityToWithInfoResponse(employee);
    }

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeMapper.requestToEntity(employeeRequestDto);
        employeeRepository.save(employee);

        return employeeMapper.entityToResponseDto(employee);
    }

    @Override
    public EmployeeResponseDto updateEmployee(UUID id, EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeRepository.getReferenceById(id);
        employeeMapper.updateRequestToEntity(employeeRequestDto, employee);

        return employeeMapper.entityToResponseDto(employee);
    }

    @Override
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.getReferenceById(id);
        employeeRepository.delete(employee);
    }
}
