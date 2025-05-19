package com.codingchallenge.controlleradvice;

import com.codingchallenge.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final Stream<String> handleConstraintViolation(ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
            .map(it -> it.getPropertyPath().toString() + " " + it.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final Stream<String> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
            .map(it -> it.getField() + " " + it.getDefaultMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final String handleNotFoundException(NotFoundException e) {
        return "404 Not Found: " + e.getMessage();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final String handleBadRequestException(BadRequestException e) {
        return "400 Bad Request: " + e.getMessage();
    }

    @ExceptionHandler(IncorrectOldPasswordException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public final String handleIncorrectOldPasswordException(IncorrectOldPasswordException e) {
        return "403 Forbidden: " + e.getMessage();
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public final String handleAlreadyExistsException(AlreadyExistsException e) {
        return "409 Conflict: " + e.getMessage();
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final String handleInternalServerErrorException(InternalServerErrorException e) {
        return "500 Internal Server Error: " + e.getMessage();
    }
}
