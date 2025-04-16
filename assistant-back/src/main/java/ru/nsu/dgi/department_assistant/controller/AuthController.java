package ru.nsu.dgi.department_assistant.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.nsu.dgi.department_assistant.domain.entity.users.Users;
import ru.nsu.dgi.department_assistant.domain.repository.auth.UserRepository;
import ru.nsu.dgi.department_assistant.domain.service.impl.AuthServiceImpl;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final AuthServiceImpl authService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            authService.refreshToken(request, response);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/success")
    public ResponseEntity<?> loginSuccess() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/oauth2/restore")
    public ResponseEntity<?> restoreOAuth2Authorization(HttpServletRequest request) {
        try {
            authService.restoreOAuth2Authorization(request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/oauth2/status")
    public ResponseEntity<?> getOAuth2Status(HttpServletRequest request) {
        return ResponseEntity.ok(authService.getOAuth2Status(request));
    }

    @GetMapping("/users")
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }
}