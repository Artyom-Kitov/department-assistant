package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.process.CommonTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.TransitionId;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommonTransitionRepository extends JpaRepository<CommonTransition, TransitionId> {
    @Query(value = """
            SELECT ct FROM CommonTransition ct
            WHERE ct.processId = ?1 AND ct.nextStepId = ?2
            """)
    List<CommonTransition> findByNextInProcess(UUID processId, int nextStepId);
}
