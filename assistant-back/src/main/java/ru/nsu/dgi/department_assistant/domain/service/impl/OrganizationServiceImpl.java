package ru.nsu.dgi.department_assistant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Organization;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.mapper.employee.OrganizationMapper;
import ru.nsu.dgi.department_assistant.domain.repository.employee.OrganizationRepository;
import ru.nsu.dgi.department_assistant.domain.service.OrganizationService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @Override
    public List<OrganizationResponseDto> getAll() {
        log.info("Getting all organizations");
        return organizationRepository.findAll().stream()
                .map(organizationMapper::entityToResponseDto)
                .toList();
    }

    @Override
    public OrganizationResponseDto getById(Integer id) {
        log.info("Getting organization by id {}", id);
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        log.info("Found organization with id {}", id);
        return organizationMapper.entityToResponseDto(organization);
    }

    @Override
    public OrganizationResponseDto create(OrganizationRequestDto request) {
        log.info("Creating organization {}", request);
        Organization organization = organizationMapper.toEntity(request);
        log.info("Saving organization {}", organization);
        return organizationMapper.entityToResponseDto(organizationRepository.save(organization));
    }

    @Override
    public OrganizationResponseDto update(Integer id, OrganizationRequestDto request) {
        log.info("Updating organization {}", request);
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        organizationMapper.updateRequestToEntity(request, organization);
        log.info("Saving updated organization {}", organization);
        return organizationMapper.entityToResponseDto(organizationRepository.save(organization));
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting organization with id {}", id);
        if (!organizationRepository.existsById(id)) {
            throw new EntityNotFoundException(String.valueOf(id));
        }
        organizationRepository.deleteById(id);
        log.info("Deleted organization with id {}", id);
    }
}

