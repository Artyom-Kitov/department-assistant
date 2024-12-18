package ru.nsu.dgi.department_assistant.domain.mapper;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.employee.ContactsResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Contacts;

@Mapper(componentModel = "spring")
public interface ContactsMapper {
    ContactsResponseDto entityToResponseDto(Contacts contacts);
}
