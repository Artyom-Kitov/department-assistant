package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentTypeResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentType;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.NullPropertyException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.EmploymentTypeMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.EmploymentTypeRepository;
import ru.nsu.dgi.department_assistant.domain.service.EmploymentTypeService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentTypeServiceImpl implements EmploymentTypeService {
    private final EmploymentTypeRepository employmentTypeRepository;
    private final EmploymentTypeMapper employmentTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EmploymentTypeResponseDto> getAll() {
        log.info("finding all employment types");
        List<EmploymentType> employmentTypes = employmentTypeRepository.findAll();
        log.info("successfully found {} employment types", employmentTypes.size());

        return employmentTypes.stream()
                .map(employmentTypeMapper::entityToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmploymentTypeResponseDto getById(Integer id) {
        log.info("finding employment type by id {}", id);
        EmploymentType employmentType = employmentTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        log.info("successfully found employment type by id {}", id);

        return employmentTypeMapper.entityToResponseDto(employmentType);
    }

    @Override
    @Transactional
    public EmploymentTypeResponseDto create(EmploymentTypeRequestDto employmentTypeRequestDto) {
        log.info("creating employment type {}", employmentTypeRequestDto.name());
        EmploymentType employmentType = employmentTypeMapper.toEntity(employmentTypeRequestDto);
        employmentTypeRepository.save(employmentType);
        log.info("successfully created employment type {}", employmentTypeRequestDto.name());

        return employmentTypeMapper.entityToResponseDto(employmentType);
    }

    @Override
    @Transactional
    public EmploymentTypeResponseDto update(Integer id, EmploymentTypeRequestDto employmentTypeRequestDto) {
        if (id == null) {
            throw new NullPropertyException("Id must not be null");
        }
        log.info("updating employment type {}", id);
        EmploymentType employmentType = employmentTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        employmentTypeMapper.updateRequestToEntity(employmentTypeRequestDto, employmentType);
        log.info("successfully updated employment type {}", id);

        return employmentTypeMapper.entityToResponseDto(employmentType);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (id == null) {
            throw new NullPropertyException("Id must not be null");
        }
        log.info("deleting employment type {}", id);
        EmploymentType employmentType = employmentTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        employmentTypeRepository.delete(employmentType);
        log.info("successfully deleted employment type {}", id);
    }
}
