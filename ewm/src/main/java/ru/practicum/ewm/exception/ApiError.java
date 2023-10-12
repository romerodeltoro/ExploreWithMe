package ru.practicum.ewm.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
@Getter
//@RequiredArgsConstructor
public class ApiError {

//    private List<Error> errors;
    private final HttpStatus status;
    private final String reason;
    private final String message;
    private final String timestamp;
}
