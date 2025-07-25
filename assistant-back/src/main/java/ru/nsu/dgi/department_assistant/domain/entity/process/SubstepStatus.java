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
import ru.nsu.dgi.department_assistant.domain.entity.documents.DocumentType;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.SubstepStatusId;

import java.util.UUID;

@Entity
@Table(name = "substep_status", schema = "proc")
@IdClass(SubstepStatusId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubstepStatus {
    @Id
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Id
    @Column(name = "start_process_id", nullable = false)
    private UUID startProcessId;

    @Id
    @Column(name = "substep_id", nullable = false)
    private UUID substepId;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", insertable = false, updatable = false)
    @JoinColumn(name = "start_process_id", referencedColumnName = "process_id", insertable = false, updatable = false)
    private EmployeeAtProcess employeeAtProcess;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "substep_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Substep substep;
}
