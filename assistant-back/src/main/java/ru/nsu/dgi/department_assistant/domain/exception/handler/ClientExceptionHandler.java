package ru.nsu.dgi.department_assistant.domain.exception.handler;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.nsu.dgi.department_assistant.domain.dto.EntityNotFoundDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.InvalidProcessTemplateDto;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;

@Slf4j
@ControllerAdvice
public class ClientExceptionHandler {

    @ExceptionHandler(InvalidProcessTemplateException.class)
    public ResponseEntity<InvalidProcessTemplateDto> handleInvalidProcessTemplateException(InvalidProcessTemplateException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(new InvalidProcessTemplateDto(e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<EntityNotFoundDto> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity with id = {} not found", e.getId());
        return new ResponseEntity<>(new EntityNotFoundDto(e.getId()),
                HttpStatus.NOT_FOUND);
    }
}
