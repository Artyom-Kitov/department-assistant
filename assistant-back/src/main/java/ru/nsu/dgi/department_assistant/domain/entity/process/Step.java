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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "step", schema = "proc")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Step {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "process_id", referencedColumnName = "id")
    private Process process;

    @Column(name = "duration", nullable = false)
    private int duration;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "meta_info", columnDefinition = "json")
    private String metaInfo;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "description", nullable = false)
    private String description;
}
