package ru.nsu.dgi.department_assistant.domain.service;


import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordResponseDto;

import java.util.List;
import java.util.UUID;

public interface CertificateOfNoCriminalRecordService {
    List<CertificateOfNoCriminalRecordResponseDto> getAll();

    CertificateOfNoCriminalRecordResponseDto getByEmployeeId(UUID id);

    CertificateOfNoCriminalRecordResponseDto update(UUID employeeId, CertificateOfNoCriminalRecordRequestDto dto);

    CertificateOfNoCriminalRecordResponseDto create(UUID employeeId, CertificateOfNoCriminalRecordRequestDto dto);

    void deleteByEmployeeId(UUID id);
}
