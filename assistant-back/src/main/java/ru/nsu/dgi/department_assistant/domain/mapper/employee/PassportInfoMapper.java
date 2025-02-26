package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.PassportInfoResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.employee.PassportInfo;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PassportInfoMapper {
    @Mapping(target = "employeeId", source = "employee", qualifiedByName = "employeeToId")
    PassportInfoResponseDto toResponse(PassportInfo passportInfo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    PassportInfo toEntity(PassportInfoRequestDto passportInfoRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(PassportInfoRequestDto dto, @MappingTarget PassportInfo passportInfo);

    @Named("employeeToId")
    default UUID employeeToId(Employee employee) {
        return employee == null ? null : employee.getId();
    }
}
