package ru.nsu.dgi.department_assistant.domain.entity;

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
    private UUID employee;
    private Integer post;
    private Integer employmentType;
}
