package ru.nsu.dgi.department_assistant.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "employee_employment", schema = "public")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(EmployeeEmploymentId.class)
public class EmployeeEmployment {
    @Id
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private UUID employeeId;

    @Id
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false)
    private Integer postId;

    @Id
    @JoinColumn(name = "employment_type_id", referencedColumnName = "id", nullable = false)
    private Integer employmentTypeId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "employment_type_id", referencedColumnName = "id")
    private EmploymentType employmentType;
}