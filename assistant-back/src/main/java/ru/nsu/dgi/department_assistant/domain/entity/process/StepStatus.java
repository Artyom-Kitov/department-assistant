package ru.nsu.dgi.department_assistant.domain.entity.process;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepStatusId;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "step_status", schema = "proc")
@IdClass(StepStatusId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StepStatus {
    @Id
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Id
    @Column(name = "process_id", nullable = false)
    private UUID processId;

    @Id
    @Column(name = "step_id", nullable = false)
    private int stepId;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "process_id", referencedColumnName = "process_id", insertable = false, updatable = false)
    @JoinColumn(name = "step_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Step step;
}
