package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationalUnitRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.OrganizationalUnitResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.OrganizationalUnit;

@Mapper(componentModel = "spring", uses = {OrganizationMapper.class, ContactsMapper.class})
public interface OrganizationalUnitMapper {
    OrganizationalUnitResponseDto entityToResponseDto(OrganizationalUnit organizationalUnit);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    OrganizationalUnit toEntity(OrganizationalUnitRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    void updateRequestToEntity(OrganizationalUnitRequestDto requestDto, @MappingTarget OrganizationalUnit organizationalUnit);
}

