package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.CertificateOfNoCriminalRecordResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.CertificateOfNoCriminalRecord;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.NullPropertyException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.CertificateOfNoCriminalRecordMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.CertificateOfNoCriminalRecordRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.service.CertificateOfNoCriminalRecordService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificateOfNoCriminalRecordServiceImpl implements CertificateOfNoCriminalRecordService {
    CertificateOfNoCriminalRecordRepository recordRepository;
    CertificateOfNoCriminalRecordMapper recordMapper;
    EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CertificateOfNoCriminalRecordResponseDto> getAll() {
        log.info("getting all certificates");
        List<CertificateOfNoCriminalRecord> certificates = recordRepository.findAll();
        log.info("found {} certificates", certificates.size());

        return certificates.stream()
                .map(recordMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateOfNoCriminalRecordResponseDto getByEmployeeId(UUID id) {
        if (id == null) {
            throw new NullPropertyException("Id must not be null");
        }
        log.info("finding certificate by employee id {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        CertificateOfNoCriminalRecord record = employee.getCertificateOfNoCriminalRecord();
        if (record == null) {
            throw new EntityNotFoundException("Certificate of no criminal record of employee with id: " + id);
        }
        log.info("found certificate by employee id {}", id);

        return recordMapper.toResponse(record);
    }

    @Override
    @Transactional
    public CertificateOfNoCriminalRecordResponseDto update(
            UUID employeeId,
            CertificateOfNoCriminalRecordRequestDto dto
    ) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("updating certificate by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        CertificateOfNoCriminalRecord record = employee.getCertificateOfNoCriminalRecord();
        if (record == null) {
            throw new EntityNotFoundException(
                    "Certificate of no criminal record of employee with id: " + employeeId
            );
        }
        recordMapper.updateRequestToEntity(dto, record);
        recordRepository.save(record);
        log.info("successfully updated certificate by employee id {}", employeeId);

        return recordMapper.toResponse(record);
    }

    @Override
    @Transactional
    public CertificateOfNoCriminalRecordResponseDto create(
            UUID employeeId,
            CertificateOfNoCriminalRecordRequestDto dto
    ) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("creating certificate by employee id {}", employeeId);
        CertificateOfNoCriminalRecord record = recordMapper.toEntity(dto);
        setEmployee(employeeId, record);
        recordRepository.save(record);
        log.info("successfully created certificate by employee id {}", employeeId);

        return recordMapper.toResponse(record);
    }

    @Override
    @Transactional
    public void deleteByEmployeeId(UUID id) {
        if (id == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("deleting certificate by employee id {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        CertificateOfNoCriminalRecord record = employee.getCertificateOfNoCriminalRecord();
        if (record == null) {
            throw new EntityNotFoundException(
                    "Certificate of no criminal record of employee with id: " + id
            );
        }
        employee.setCertificateOfNoCriminalRecord(null);
        recordRepository.delete(record);
        log.info("successfully deleted certificate by employee id {}", id);
    }

    private void setEmployee(UUID employeeId, CertificateOfNoCriminalRecord record) {
        log.info("finding an employee by id {} for certificate", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        log.info("found employee with id {} for certificate", employeeId);
        record.setEmployee(employee);
        employee.setCertificateOfNoCriminalRecord(record);
    }
}
