package ru.nsu.dgi.department_assistant.domain.exception;

import java.io.IOException;

public class FileServiceException extends IOException {
    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
