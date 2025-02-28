package ru.nsu.dgi.department_assistant.domain.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.nsu.dgi.department_assistant.domain.dto.EntityNotFoundDto;
import ru.nsu.dgi.department_assistant.domain.dto.InvalidRequestDto;
import ru.nsu.dgi.department_assistant.domain.dto.process.InvalidProcessTemplateDto;
import ru.nsu.dgi.department_assistant.domain.exception.EntityAlreadyExistsException;
import ru.nsu.dgi.department_assistant.domain.exception.EntityNotFoundException;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;
import ru.nsu.dgi.department_assistant.domain.exception.NullPropertyException;

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

    @ExceptionHandler(NullPropertyException.class)
    public ResponseEntity<InvalidRequestDto> handleNullPropertyException(NullPropertyException e) {
        log.error("Null property exception handled:", e);
        return new ResponseEntity<>(new InvalidRequestDto(
                "Expected non-null property/properties but got null. Message: " + e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<InvalidRequestDto> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        log.error("Entity already exists exception handled:", e);
        return new ResponseEntity<>(
                new InvalidRequestDto("Entity you're trying to create is already exists. Message: " + e.getMessage()),
                HttpStatus.PRECONDITION_FAILED
        );
    }
}
