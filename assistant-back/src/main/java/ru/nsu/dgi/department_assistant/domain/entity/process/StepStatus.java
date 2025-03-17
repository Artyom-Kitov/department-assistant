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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@ToString
@Builder
public class StepStatus {
    @Id
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Id
    @Column(name = "start_process_id", nullable = false)
    private UUID startProcessId;

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

    @Column(name = "is_successful")
    private Boolean isSuccessful;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", insertable = false, updatable = false)
    @JoinColumn(name = "start_process_id", referencedColumnName = "process_id", insertable = false, updatable = false)
    private EmployeeAtProcess employeeAtProcess;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "process_id", referencedColumnName = "process_id", insertable = false, updatable = false)
    @JoinColumn(name = "step_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Step step;

    @Builder
    public StepStatus(UUID employeeId, UUID processId, int stepId, UUID startProcessId, LocalDate deadline, LocalDate completedAt,
                      Boolean isSuccessful) {
        this.employeeId = employeeId;
        this.processId = processId;
        this.stepId = stepId;
        this.startProcessId = startProcessId;
        this.deadline = deadline;
        this.completedAt = completedAt;
        this.isSuccessful = isSuccessful;
    }
}
