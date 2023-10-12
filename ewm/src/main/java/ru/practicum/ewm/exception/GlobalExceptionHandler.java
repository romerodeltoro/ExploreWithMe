/*
package ru.practicum.ewm.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j

@RestControllerAdvice

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})

    public ResponseEntity<ApiError> handleBadRequest(Exception ex) throws IOException {

        ApiError apiError = new ApiError(

                HttpStatus.BAD_REQUEST,

                "Данные в реквесте невалидны",

                ex.getLocalizedMessage(),

                Collections.singletonList(error(ex))

        );

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

    }

    @Override

    public @NonNull ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,

                                                                        @NonNull HttpHeaders headers,

                                                                        @NonNull HttpStatus status,

                                                                        @NonNull WebRequest request) {

        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {

            errors.add("");

        }

        ApiError apiError = new ApiError(

                HttpStatus.BAD_REQUEST,

                "Данные в реквесте невалидны",

                ex.getLocalizedMessage(),

                errors

        );

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

    }

// @ExceptionHandler({ ConstraintViolationException.class })

// public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {

// List<String> errors = new ArrayList<>();

// for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {

// errors.add("");

// }

//

// ApiError apiError = new ApiError(

// HttpStatus.BAD_REQUEST,

// "Данные в реквесте невалидны",

// ex.getLocalizedMessage(),

// errors

// );

//

// return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

// }

    private String error(Exception e) throws IOException {

        StringWriter sw = new StringWriter();

        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        String error = sw.toString();

        sw.close();

        pw.close();

        return error;

    }

}
*/
