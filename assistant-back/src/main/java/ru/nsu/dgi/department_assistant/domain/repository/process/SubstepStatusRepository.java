package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.dgi.department_assistant.domain.entity.process.SubstepStatus;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.SubstepStatusId;

public interface SubstepStatusRepository extends JpaRepository<SubstepStatus, SubstepStatusId> {
}
