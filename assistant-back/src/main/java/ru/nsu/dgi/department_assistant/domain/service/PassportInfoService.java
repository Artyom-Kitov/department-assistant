package ru.nsu.dgi.department_assistant.domain.service;

import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoResponseDto;

import java.util.List;
import java.util.UUID;

public interface PassportInfoService {
    List<PassportInfoResponseDto> getAll();

    PassportInfoResponseDto getByEmployeeId(UUID employeeId);

    PassportInfoResponseDto create(UUID employeeId, PassportInfoRequestDto passportInfoRequestDto);

    PassportInfoResponseDto update(UUID employeeId, PassportInfoRequestDto passportInfoRequestDto);

    void delete(UUID employeeId);
}
