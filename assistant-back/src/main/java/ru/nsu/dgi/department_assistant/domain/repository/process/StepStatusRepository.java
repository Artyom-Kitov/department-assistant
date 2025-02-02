package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.dgi.department_assistant.domain.entity.process.StepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.StepStatusId;

public interface StepStatusRepository extends JpaRepository<StepStatus, StepStatusId> {
}
