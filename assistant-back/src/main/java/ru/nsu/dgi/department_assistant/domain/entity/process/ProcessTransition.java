package ru.nsu.dgi.department_assistant.domain.entity.process;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.TransitionId;

import java.util.UUID;

@Entity
@Table(name = "process_transition", schema = "proc")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TransitionId.class)
public class ProcessTransition {
    @Id
    @Column(name = "process_id", nullable = false)
    private UUID processId;

    @Id
    @Column(name = "step_id", nullable = false)
    private int stepId;

    @Column(name = "next_process_id", nullable = false)
    private UUID nextProcessId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "process_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Process process;
}
