package ru.nsu.dgi.department_assistant.domain.exception.handler;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.nsu.dgi.department_assistant.domain.dto.process.InvalidProcessTemplateDto;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;

@Slf4j
@Hidden
@ControllerAdvice
public class ClientExceptionHandler {

    @ExceptionHandler(InvalidProcessTemplateException.class)
    public ResponseEntity<InvalidProcessTemplateDto> handleInvalidProcessTemplateException(InvalidProcessTemplateException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(new InvalidProcessTemplateDto(e.getMessage()));
    }
}
