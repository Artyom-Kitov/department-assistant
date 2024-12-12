package ru.nsu.dgi.department_assistant.domain.entity.process;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "conditional_transition", schema = "proc")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConditionalTransition {
    @Id
    @Column(name = "step_id", nullable = false)
    private UUID stepId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "step_id", referencedColumnName = "id")
    private Step step;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "positive_step_id", referencedColumnName = "id")
    private Step positiveStep;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "negative_step_id", referencedColumnName = "id")
    private Step negativeStep;
}
