package ru.nsu.dgi.department_assistant.domain.entity.process;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Employee;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "execution_history", schema = "proc")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionHistory {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "process_id", nullable = false)
    private UUID processId;

    @Column(name = "started_at", nullable = false)
    private LocalDate startedAt;

    @Column(name = "completed_at", nullable = false)
    private LocalDate completedAt;

    @Column(name = "is_successful", nullable = false)
    private boolean isSuccessful;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "process_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Process process;
}
