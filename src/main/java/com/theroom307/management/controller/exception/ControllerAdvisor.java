package com.theroom307.management.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleProductNotFoundException(
            ResponseStatusException exception
    ) {
        return new ResponseEntity<>(
                exception.getReason(),
                exception.getStatusCode()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedException() {
        return new ResponseEntity<>(
                "Sorry, something went wrong",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
