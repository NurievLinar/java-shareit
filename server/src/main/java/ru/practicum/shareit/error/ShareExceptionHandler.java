/*
package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedException;
import ru.practicum.shareit.exceptions.ValidationException;

@RestControllerAdvice
@Slf4j
public class ShareExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.info("Нет данных.");
        return new ErrorResponse(
                e.getMessage(), "Нет данных."
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedException(final UnsupportedException e) {
        log.info("Unknown state: UNSUPPORTED_STATUS");
        return new ErrorResponse(
                e.getMessage(), "Unknown state: UNSUPPORTED_STATUS"
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.info("Ошибка валидации.");
        return new ErrorResponse(
                e.getMessage(), "Ошибка валидации."
        );
    }
}*/
