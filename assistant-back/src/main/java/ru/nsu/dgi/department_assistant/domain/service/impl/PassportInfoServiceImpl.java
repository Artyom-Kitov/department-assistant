package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.PassportInfo;
import ru.nsu.dgi.department_assistant.domain.exception.EntityAlreadyExistsException;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.NullPropertyException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.PassportInfoMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmployeeRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.PassportInfoRepository;
import ru.nsu.dgi.department_assistant.domain.service.PassportInfoService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassportInfoServiceImpl implements PassportInfoService {
    private final PassportInfoRepository passportInfoRepository;
    private final PassportInfoMapper passportInfoMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PassportInfoResponseDto> getAll() {
        log.info("finding all passport infos");
        List<PassportInfo> passportInfos = passportInfoRepository.findAll();
        log.info("successfully found {} passport infos", passportInfos.size());

        return passportInfos.stream()
                .map(passportInfoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PassportInfoResponseDto getByEmployeeId(UUID employeeId) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("finding passport info by employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        PassportInfo info = employee.getPassportInfo();
        if (info == null) {
            throw new EntityNotFoundException("Could not find passport info for employee id " + employeeId);
        }
        log.info("successfully found passport info for employee id {}", employeeId);

        return passportInfoMapper.toResponse(info);
    }

    @Override
    @Transactional
    public PassportInfoResponseDto create(UUID employeeId, PassportInfoRequestDto passportInfoRequestDto) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("creating passport info for employee id {}", employeeId);
        PassportInfo passportInfo = passportInfoMapper.toEntity(passportInfoRequestDto);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        if (employee.getPassportInfo() != null) {
            throw new EntityAlreadyExistsException(
                    "Passport info for employee id " + employeeId + " already exists"
            );
        }
        passportInfo.setEmployee(employee);
        employee.setPassportInfo(passportInfo);
        passportInfoRepository.save(passportInfo);
        log.info("successfully created passport info for employee id {}", employeeId);

        return passportInfoMapper.toResponse(passportInfo);
    }

    @Override
    @Transactional
    public PassportInfoResponseDto update(UUID employeeId, PassportInfoRequestDto passportInfoRequestDto) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("updating passport info for employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        PassportInfo info = employee.getPassportInfo();
        if (info == null) {
            throw new EntityNotFoundException("Could not find passport info for employee id " + employeeId);
        }
        passportInfoMapper.updateRequestToEntity(passportInfoRequestDto, info);
        passportInfoRepository.save(info);
        log.info("successfully updated passport info for employee id {}", employeeId);

        return passportInfoMapper.toResponse(info);
    }

    @Override
    @Transactional
    public void delete(UUID employeeId) {
        if (employeeId == null) {
            throw new NullPropertyException("EmployeeId must not be null");
        }
        log.info("deleting passport info for employee id {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(employeeId)));
        PassportInfo info = employee.getPassportInfo();
        if (info == null) {
            throw new EntityNotFoundException("Could not find passport info for employee id " + employeeId);
        }
        passportInfoRepository.delete(info);
        log.info("successfully deleted passport info for employee id {}", employeeId);
    }
}
