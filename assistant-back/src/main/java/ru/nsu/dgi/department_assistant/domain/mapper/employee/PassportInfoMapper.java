package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.PassportInfo;

@Mapper(componentModel = "spring")
public interface PassportInfoMapper {
    PassportInfoResponseDto entityToDto(PassportInfo passportInfo);
}
