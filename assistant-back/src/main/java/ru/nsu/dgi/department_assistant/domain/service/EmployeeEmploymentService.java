package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmployeeEmploymentResponseDto;

import java.util.List;
import java.util.UUID;

public interface EmployeeEmploymentService {
    List<EmployeeEmploymentResponseDto> getAll();

    List<EmployeeEmploymentResponseDto> getByEmployeeId(UUID id);

    EmployeeEmploymentResponseDto getById(UUID employeeId, Integer postId, Integer employmentTypeId);

    EmployeeEmploymentResponseDto create(
            UUID employeeId,
            Integer postId,
            Integer employmentTypeId
    );

    EmployeeEmploymentResponseDto update(
            UUID employeeId,
            Integer postId,
            Integer employmentTypeId,
            EmployeeEmploymentRequestDto employmentRequestDto
    );

    void delete(
            UUID employeeId,
            Integer postId,
            Integer employmentTypeId
    );
}
