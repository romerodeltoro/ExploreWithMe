package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ApiError>> notValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        final List<ApiError> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> ApiError.builder()
                        .message(error.getDefaultMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(DateTimeFormatter.ofPattern(PATTERN).format(LocalDateTime.now()))
                        .reason(error.getField())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ApiError>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage());
        final List<ApiError> violations = e.getConstraintViolations().stream()
                .map(error -> ApiError.builder()
                        .message(error.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(DateTimeFormatter.ofPattern(PATTERN).format(LocalDateTime.now()))
                        .reason(error.getPropertyPath().toString().replaceAll("(.*)\\.", ""))
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(violations);
    }

    @ExceptionHandler(CategoriesNotFoundException.class)
    public ResponseEntity<ApiError> categoriesNotFoundException(CategoriesNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .reason("The required object was not found")
                        .message(e.getMessage())
                        .timestamp(DateTimeFormatter.ofPattern(PATTERN).format(LocalDateTime.now()))
                        .build());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> userNotFoundException(UserNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .reason("The required object was not found")
                        .message(e.getMessage())
                        .timestamp(DateTimeFormatter.ofPattern(PATTERN).format(LocalDateTime.now()))
                        .build());
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiError> eventNotFoundException(EventNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .reason("The required object was not found")
                        .message(e.getMessage())
                        .timestamp(DateTimeFormatter.ofPattern(PATTERN).format(LocalDateTime.now()))
                        .build());
    }

    @ExceptionHandler(EventValidateException.class)
    public ResponseEntity<List<ApiError>> eventValidateException(EventValidateException e) {
        log.error(e.getMessage());
        final List<ApiError> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> ApiError.builder()
                        .message(error.getDefaultMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(DateTimeFormatter.ofPattern(PATTERN).format(LocalDateTime.now()))
                        .reason(error.getField())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(violations);
    }

    @ExceptionHandler(EventDateValidateException.class)
    public ResponseEntity<ApiError> eventDateValidateException(EventDateValidateException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiError.builder()
                        .status(HttpStatus.FORBIDDEN)
                        .reason("For the requested operation the conditions are not met")
                        .message(e.getMessage())
                        .timestamp(DateTimeFormatter.ofPattern(PATTERN).format(LocalDateTime.now()))
                        .build());
    }
}
