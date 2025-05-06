package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationalUnitRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationalUnitResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Organization;
import ru.nsu.dgi.department_assistant.domain.entity.employee.OrganizationalUnit;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.OrganizationalUnitMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.OrganizationRepository;
import ru.nsu.dgi.department_assistant.domain.repository.employee.OrganizationalUnitRepository;
import ru.nsu.dgi.department_assistant.domain.service.OrganizationalUnitService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationalUnitServiceImpl implements OrganizationalUnitService {
    private final OrganizationalUnitRepository organizationalUnitRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationalUnitMapper organizationalUnitMapper;

    @Override
    public List<OrganizationalUnitResponseDto> getAll() {
        log.info("Getting all organizational units");
        return organizationalUnitRepository.findAll().stream()
                .map(organizationalUnitMapper::entityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrganizationalUnitResponseDto getById(Long id) {
        log.info("Getting organizational unit by id {}", id);
        OrganizationalUnit organizationalUnit = organizationalUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        log.info("Found organizational unit with id {}", id);
        return organizationalUnitMapper.entityToResponseDto(organizationalUnit);
    }

    @Override
    public OrganizationalUnitResponseDto create(OrganizationalUnitRequestDto request) {
        log.info("Creating organizational unit {}", request);
        OrganizationalUnit organizationalUnit = organizationalUnitMapper.toEntity(request);
        if (request.organizationId() != null) {
            Organization organization = organizationRepository.findById(request.organizationId())
                    .orElseThrow(() -> new EntityNotFoundException(String.valueOf(request.organizationId())));
            organizationalUnit.setOrganization(organization);
        }
        log.info("Saving organizational unit {}", organizationalUnit);
        return organizationalUnitMapper.entityToResponseDto(organizationalUnitRepository.save(organizationalUnit));
    }

    @Override
    public OrganizationalUnitResponseDto update(Long id, OrganizationalUnitRequestDto request) {
        log.info("Updating organizational unit {}", request);
        OrganizationalUnit organizationalUnit = organizationalUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));

        organizationalUnitMapper.updateRequestToEntity(request, organizationalUnit);
        if (request.organizationId() != null) {
            Organization organization = organizationRepository.findById(request.organizationId())
                    .orElseThrow(() -> new EntityNotFoundException(String.valueOf(request.organizationId())));
            organizationalUnit.setOrganization(organization);
        }
        log.info("Saving updated organizational unit {}", organizationalUnit);
        return organizationalUnitMapper.entityToResponseDto(organizationalUnitRepository.save(organizationalUnit));
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting organizational unit with id {}", id);
        if (!organizationalUnitRepository.existsById(id)) {
            throw new EntityNotFoundException(String.valueOf(id));
        }
        organizationalUnitRepository.deleteById(id);
        log.info("Deleted organizational unit with id {}", id);
    }
}

