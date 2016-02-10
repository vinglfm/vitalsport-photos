package com.vitalsport.photos.web;

import org.springframework.hateoas.VndErrors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@ResponseBody
public class ExceptionControllerAdvice {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public VndErrors.VndError illegalArgumentExceptionHandler(IllegalArgumentException exception) {
        String message = Optional.of(exception.getMessage()).orElse(exception.getClass().getSimpleName());
        return new VndErrors.VndError(exception.getLocalizedMessage(), message);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalError.class)
    public VndErrors.VndError internalError(InternalError exception) {
        String message = Optional.of(exception.getCause().getMessage()).orElse(exception.getClass().getSimpleName());
        return new VndErrors.VndError(exception.getLocalizedMessage(), message);
    }
}
