package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Contacts;

@Mapper(componentModel = "spring")
public interface ContactsMapper {
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
}
