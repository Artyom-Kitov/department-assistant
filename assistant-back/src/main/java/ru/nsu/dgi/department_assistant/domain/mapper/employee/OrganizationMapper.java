package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Organization;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    OrganizationResponseDto entityToResponseDto(Organization organization);
    Organization toEntity(OrganizationRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(OrganizationRequestDto requestDto, @MappingTarget Organization organization);
}

