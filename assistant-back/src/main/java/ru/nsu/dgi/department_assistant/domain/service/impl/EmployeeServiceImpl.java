package ru.nsu.dgi.department_assistant.domain.service.impl;

import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeResponseDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeWithAllInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.EmployeeMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.service.EmployeeService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .map(employeeMapper::entityToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(id.toString())
        );

        return employeeMapper.entityToResponseDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeWithAllInfoResponseDto> getAllEmployeeWithAllInfos() {
        List<Employee> employees = employeeRepository.findAllEmployeesWithInfo();

        return employees.stream()
                .map(employeeMapper::entityToWithInfoResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeWithAllInfoResponseDto getEmployeeWithAllInfos(UUID id) {
        Employee employee = employeeRepository.findEmployeeWithInfoById(id).orElseThrow(
                () -> new EntityNotFoundException(id.toString())
        );

        return employeeMapper.entityToWithInfoResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDto createEmployee(EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeMapper.requestToEntity(employeeRequestDto);
        employeeRepository.save(employee);

        return employeeMapper.entityToResponseDto(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDto updateEmployee(UUID id, EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(id.toString())
        );

        // Проверяем уникальность inn
        if (employeeRequestDto.inn() != null) {
            Optional<Employee> employeeWithSameInn = employeeRepository.findByInn(employeeRequestDto.inn());
            if (employeeWithSameInn.isPresent() && !employeeWithSameInn.get().getId().equals(id)) {
                throw new NonUniqueResultException("Employee with inn " + employeeRequestDto.inn() + " already exists");
            }
        }

        // Проверяем уникальность snils
        if (employeeRequestDto.snils() != null) {
            Optional<Employee> employeeWithSameSnils = employeeRepository.findBySnils(employeeRequestDto.snils());
            if (employeeWithSameSnils.isPresent() && !employeeWithSameSnils.get().getId().equals(id)) {
                throw new NonUniqueResultException("Employee with snils " + employeeRequestDto.snils() + " already exists");
            }
        }

        employeeMapper.updateRequestToEntity(employeeRequestDto, employee);
        employeeRepository.save(employee);

        return employeeMapper.entityToResponseDto(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException(id.toString());
        }

        employeeRepository.deleteById(id);
    }
}
