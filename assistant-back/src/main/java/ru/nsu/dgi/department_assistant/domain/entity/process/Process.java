package ru.nsu.dgi.department_assistant.domain.entity.process;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "process", schema = "proc")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Process {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "total_duration", nullable = false)
    private int totalDuration;
}
