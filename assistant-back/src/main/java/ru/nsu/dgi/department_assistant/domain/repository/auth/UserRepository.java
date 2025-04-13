package ru.nsu.dgi.department_assistant.domain.repository.auth;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
