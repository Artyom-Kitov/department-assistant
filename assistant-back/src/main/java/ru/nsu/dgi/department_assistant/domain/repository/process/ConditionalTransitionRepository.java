package ru.nsu.dgi.department_assistant.domain.repository.process;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.process.ConditionalTransition;
import ru.nsu.dgi.department_assistant.domain.entity.process.id.TransitionId;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConditionalTransitionRepository extends JpaRepository<ConditionalTransition, TransitionId> {
    @Query(value = """
            SELECT ct FROM ConditionalTransition ct
            WHERE ct.processId = ?1 AND (ct.positiveStepId = ?2 OR ct.negativeStepId = ?2)
            """)
    List<ConditionalTransition> findByNextInProcess(UUID processId, int nextStepId);
}
