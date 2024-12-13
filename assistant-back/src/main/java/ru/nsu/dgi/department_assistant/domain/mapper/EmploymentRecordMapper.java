package ru.nsu.dgi.department_assistant.domain.mapper;

import org.mapstruct.Mapper;
import ru.nsu.dgi.department_assistant.domain.dto.EmploymentRecordResponseDto;
import ru.nsu.dgi.department_assistant.domain.entity.employee.EmploymentRecord;

@Mapper(componentModel = "spring")
public interface EmploymentRecordMapper {
    EmploymentRecordResponseDto entityToResponseDto(EmploymentRecord employmentRecord);
}
