package ru.nsu.dgi.department_assistant.domain.entity.employee;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmployeeEmploymentId implements Serializable {
    private UUID employeeId;
    private Integer postId;
    private Integer employmentTypeId;
}
