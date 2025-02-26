package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.WorkExperienceResponseDto;

import java.util.List;
import java.util.UUID;

public interface WorkExperienceService {
    List<WorkExperienceResponseDto> getAll();

    WorkExperienceResponseDto getByEmployeeId(UUID employeeId);

    WorkExperienceResponseDto create(UUID employeeId, WorkExperienceRequestDto workExperienceRequestDto);

    WorkExperienceResponseDto update(UUID employeeId, WorkExperienceRequestDto workExperienceRequestDto);

    void delete(UUID employeeId);
}
