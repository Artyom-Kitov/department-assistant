package ru.nsu.dgi.department_assistant.domain.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
@Embeddable
public class EmployeeEmploymentId implements Serializable {
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "post_id", nullable = false)
    private Integer postId;

    @Column(name = "employment_type_id", nullable = false)
    private Integer employmentTypeId;
}
