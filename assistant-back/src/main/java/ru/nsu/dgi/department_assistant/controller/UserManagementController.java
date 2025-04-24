package ru.nsu.dgi.department_assistant.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.nsu.dgi.department_assistant.domain.dto.user.RoleUpdateRequest;
import ru.nsu.dgi.department_assistant.domain.dto.user.UserDto;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;
import ru.nsu.dgi.department_assistant.domain.service.impl.AuthServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
//@PreAuthorize("hasRole('USER')")
public class UserManagementController {
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody RoleUpdateRequest request) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRole(request.getRole());
                    Users updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(UserDto.fromUser(updatedUser));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
