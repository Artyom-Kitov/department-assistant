package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Contacts;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.OrganizationalUnit;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ContactsMapper {
    @Mapping(target = "employeeId", source = "employee", qualifiedByName = "employeeToId")
    @Mapping(target = "organizationalUnitId", source = "organizationalUnit", qualifiedByName = "orgUnitToId")
    ContactsResponseDto entityToResponseDto(Contacts contacts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "organizationalUnit", ignore = true)
    Contacts toEntity(ContactsRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "organizationalUnit", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(
            ContactsRequestDto contactsRequestDto,
            @MappingTarget Contacts contacts
    );

    @Named("employeeToId")
    default UUID employeeToId(Employee employee) {
        return employee == null ? null : employee.getId();
    }

    @Named("orgUnitToId")
    default Long orgUnitToId(OrganizationalUnit organizationalUnit) {
        return organizationalUnit == null ? null : organizationalUnit.getId();
    }
}
