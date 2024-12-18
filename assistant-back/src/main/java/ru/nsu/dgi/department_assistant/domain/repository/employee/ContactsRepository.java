package ru.nsu.dgi.department_assistant.domain.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.employee.Contacts;

import java.util.Optional;

@Repository
public interface ContactsRepository extends JpaRepository<Contacts, Long> {
    Optional<Contacts> findByEmail(String email);
    Optional<Contacts> findByNsuEmail(String nsuEmail);
}
