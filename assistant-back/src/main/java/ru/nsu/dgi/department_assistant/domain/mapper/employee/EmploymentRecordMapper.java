package ru.nsu.dgi.department_assistant.domain.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentRecordRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.employee.EmploymentRecordResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentRecord;

@Mapper(componentModel = "spring")
public interface EmploymentRecordMapper {
    EmploymentRecordResponseDto entityToResponseDto(EmploymentRecord employmentRecord);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    EmploymentRecord toEntity(EmploymentRecordRequestDto employmentRecordRequestDto);
}
