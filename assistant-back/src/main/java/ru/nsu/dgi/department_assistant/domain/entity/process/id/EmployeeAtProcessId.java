package ru.nsu.dgi.department_assistant.domain.entity.process.id;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EmployeeAtProcessId implements Serializable {
    private UUID employeeId;
    private UUID processId;
}
