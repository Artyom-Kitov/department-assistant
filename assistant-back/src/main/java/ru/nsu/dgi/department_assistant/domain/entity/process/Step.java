package ru.nsu.dgi.department_assistant.domain.entity.process;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepId;

import java.util.UUID;

@Entity
@Table(name = "step", schema = "proc")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(StepId.class)
public class Step {
    @Id
    @Column(name = "id")
    private int id;

    @Id
    @Column(name = "process_id")
    private UUID processId;

    @Column(name = "duration", nullable = false)
    private int duration;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "meta_info", columnDefinition = "json")
    private JsonNode metaInfo;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "description", nullable = false)
    private String description;

    public StepId getStepId() {
        return new StepId(id, processId);
    }
}
