package ru.nsu.dgi.department_assistant.domain.entity.process.id;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SubstepStatusId implements Serializable {
    private UUID employeeId;
    private UUID startProcessId;
    private UUID substepId;
}
