package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentStatus;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.EmploymentStatusMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmploymentStatusRepository;
import ru.nsu.dgi.department_assistant.domain.service.EmploymentStatusService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmploymentStatusServiceImpl implements EmploymentStatusService {
    private final EmploymentStatusRepository employmentStatusRepository;
    private final EmploymentStatusMapper employmentStatusMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmploymentStatusResponseDto> getAll() {
        log.info("finding all employment statuses");
        List<EmploymentStatus> employmentStatuses = employmentStatusRepository.findAll();
        log.info("successfully found {} employment statuses", employmentStatuses.size());

        return employmentStatuses.stream()
                .map(employmentStatusMapper::entityToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmploymentStatusResponseDto getByEmployeeId(UUID employeeId) {
        if (employeeId == null) {
            throw new NullPointerException("EmployeeId must not be null");
        }
        log.info("finding employment status by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        EmploymentStatus status = employee.getEmploymentStatus();
        if (status == null) {
            throw new EntityNotFoundException("Could not find employment status by employee id " + employeeId);
        }
        log.info("successfully found employment status by employee id {}", employeeId);

        return employmentStatusMapper.entityToResponseDto(status);
    }

    @Override
    @Transactional
    public EmploymentStatusResponseDto create(UUID employeeId, EmploymentStatusRequestDto employmentStatusRequestDto) {
        if (employeeId == null) {
            throw new NullPointerException("EmployeeId must not be null");
        }
        log.info("creating employment status by employee id {}", employeeId);
        EmploymentStatus status = employmentStatusMapper.toEntity(employmentStatusRequestDto);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        employee.setEmploymentStatus(status);
        status.setEmployee(employee);
        employmentStatusRepository.save(status);
        log.info("successfully created employment status by employee id {}", employeeId);

        return employmentStatusMapper.entityToResponseDto(status);
    }

    @Override
    @Transactional
    public EmploymentStatusResponseDto update(UUID employeeId, EmploymentStatusRequestDto employmentStatusRequestDto) {
        if (employeeId == null) {
            throw new NullPointerException("EmployeeId must not be null");
        }
        log.info("updating employment status by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        EmploymentStatus status = employee.getEmploymentStatus();
        if (status == null) {
            throw new EntityNotFoundException("Could not find employment status by employee id " + employeeId);
        }
        employmentStatusMapper.updateRequestToEntity(employmentStatusRequestDto, status);
        employmentStatusRepository.save(status);
        log.info("successfully updated employment status by employee id {}", employeeId);

        return employmentStatusMapper.entityToResponseDto(status);
    }

    @Override
    @Transactional
    public void delete(UUID employeeId) {
        if (employeeId == null) {
            throw new NullPointerException("EmployeeId must not be null");
        }
        log.info("deleting employment status by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        EmploymentStatus status = employee.getEmploymentStatus();
        if (status == null) {
            throw new EntityNotFoundException("Could not find employment status by employee id " + employeeId);
        }
        employmentStatusRepository.delete(status);
        log.info("successfully deleted employment status by employee id {}", employeeId);
    }
}
