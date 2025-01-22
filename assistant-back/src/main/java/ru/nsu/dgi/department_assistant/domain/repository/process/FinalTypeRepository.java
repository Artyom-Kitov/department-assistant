package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.process.FinalType;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.TransitionId;

@Repository
public interface FinalTypeRepository extends JpaRepository<FinalType, TransitionId> {
}
