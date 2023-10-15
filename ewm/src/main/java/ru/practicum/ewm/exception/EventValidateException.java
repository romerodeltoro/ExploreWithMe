package ru.practicum.ewm.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

public class EventValidateException extends BindException {

    public EventValidateException(BindingResult bindingResult) {
        super(bindingResult);
    }

//    public EventValidateException(Object target, String objectName) {
//        super(target, objectName);
//    }
}
