package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentStatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface EmploymentStatusService {
    List<EmploymentStatusResponseDto> getAll();

    EmploymentStatusResponseDto getByEmployeeId(UUID employeeId);

    EmploymentStatusResponseDto create(UUID employeeId, EmploymentStatusRequestDto employmentStatusRequestDto);

    EmploymentStatusResponseDto update(UUID employeeId, EmploymentStatusRequestDto employmentStatusRequestDto);

    void delete(UUID employeeId);
}
