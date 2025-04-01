package ru.nsu.dgi.department_assistant.domain.exception.handler;

import jakarta.persistence.NonUniqueResultException;
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
import ru.nsu.dgi.department_assistant.domain.exception.StorageFileException;
import ru.nsu.dgi.department_assistant.domain.exception.InvalidProcessTemplateException;
import ru.nsu.dgi.department_assistant.domain.exception.NullPropertyException;
import ru.nsu.dgi.department_assistant.domain.exception.StorageCreationFailureException;

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
        log.error(e.getClass().getSimpleName() + ": ", e);
        return new ResponseEntity<>(
                new InvalidRequestDto("Expected non-null property/properties but got null. Message: " + e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<InvalidRequestDto> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        log.error(e.getClass().getSimpleName() + ": ", e);
        return new ResponseEntity<>(
                new InvalidRequestDto("Entity you're trying to create already exists. Message: " + e.getMessage()),
                HttpStatus.PRECONDITION_FAILED
        );
    }

    @ExceptionHandler(NonUniqueResultException.class)
    public ResponseEntity<InvalidRequestDto> handleNonUniqueResultException(NonUniqueResultException e) {
        log.error(e.getClass().getSimpleName() + ": ", e);
        return new ResponseEntity<>(
                new InvalidRequestDto(
                        "Your request violates unique constraints of an entity you're trying to change. Message: " +
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(StorageCreationFailureException.class)
    public ResponseEntity<InvalidRequestDto> handleStorageCreationFailureException(StorageCreationFailureException e) {
        log.error(e.getClass().getSimpleName() + ": ", e);
        return new ResponseEntity<>(
                new InvalidRequestDto(
                        "Failed to create file storage. Message: " + e.getMessage() + "; cause: " + e.getCause()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(StorageFileException.class)
    public ResponseEntity<InvalidRequestDto> handleStorageFileException(StorageFileException e) {
        log.error(e.getClass().getSimpleName() + ": ", e);
        return new ResponseEntity<>(
                new InvalidRequestDto(
                        "Failed to do an action with a file. Message: " + e.getMessage() + "; cause: " + e.getCause()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
