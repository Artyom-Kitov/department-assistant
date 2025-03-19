package ru.nsu.dgi.department_assistant.domain.entity.document;

import jakarta.persistence.*;
import lombok.*;
import ru.nsu.dgi.department_assistant.config.TemplateType;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
    public class DocumentTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mimeName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateType templateType;

    @Column
    private String subject;

    @Column
    private String description;



}
