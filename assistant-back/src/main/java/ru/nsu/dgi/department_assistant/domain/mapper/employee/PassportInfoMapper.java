package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.PassportInfo;

@Mapper(componentModel = "spring")
public interface PassportInfoMapper {
    PassportInfoResponseDto entityToDto(PassportInfo passportInfo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    PassportInfo toEntity(PassportInfoRequestDto passportInfoRequestDto);
}
