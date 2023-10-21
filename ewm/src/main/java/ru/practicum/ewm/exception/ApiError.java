package ru.practicum.ewm.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ApiError {

    private final HttpStatus status;
    private final String reason;
    private final String message;
    private final String timestamp;
}
