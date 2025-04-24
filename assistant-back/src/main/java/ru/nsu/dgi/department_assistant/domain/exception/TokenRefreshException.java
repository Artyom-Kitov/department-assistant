package ru.nsu.dgi.department_assistant.domain.exception;

import lombok.Getter;

@Getter
public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String message) {
        super(message);
    }

}