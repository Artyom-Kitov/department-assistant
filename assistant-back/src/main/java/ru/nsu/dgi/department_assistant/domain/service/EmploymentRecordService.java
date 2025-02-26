package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentRecordRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentRecordResponseDto;

import java.util.List;
import java.util.UUID;

public interface EmploymentRecordService {
    List<EmploymentRecordResponseDto> getAll();

    EmploymentRecordResponseDto getByEmployeeId(UUID id);

    EmploymentRecordResponseDto create(UUID employeeId, EmploymentRecordRequestDto employmentRecordRequestDto);

    EmploymentRecordResponseDto update(UUID employeeId, EmploymentRecordRequestDto employmentRecordRequestDto);

    void deleteByEmployeeId(UUID employeeId);
}
