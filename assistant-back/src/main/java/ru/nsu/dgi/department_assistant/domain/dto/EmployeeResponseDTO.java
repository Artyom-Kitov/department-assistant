package ru.nsu.dgi.department_assistant.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String middleName;
    private Boolean agreement;
    private Boolean hasCompletedAdvancedCourses;
    private Boolean needsMandatoryElection;
    private String snils;
    private String inn;
    private Boolean isArchived;
}
