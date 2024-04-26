package com.neo.exceptions;

import com.neo.exceptions.validation.AgeException;
import com.neo.exceptions.validation.UserNotFoundException;
import com.neo.exceptions.validation.ValidationErrorResponse;
import com.neo.exceptions.validation.Violation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestControllerAdvice
public class GeneralExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> {
                    String propertyPath = violation.getPropertyPath().toString();
                    String fieldName = propertyPath.substring(propertyPath.lastIndexOf(".") + 1);
                    return new Violation(fieldName, violation.getMessage());
                })
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AgeException.class)
    public ValidationErrorResponse onMethodAgeNotValidException(Exception e) {
        return new ValidationErrorResponse(List.of(new Violation("birthDate", e.getMessage())));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public ValidationErrorResponse onMethodUserIdNotValidException(Exception e) {
        return new ValidationErrorResponse(List.of(new Violation("userId", e.getMessage())));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ValidationErrorResponse onMethodDateRangeIsNotValid(Exception e) {
        return new ValidationErrorResponse(List.of(new Violation("fromDate", e.getMessage())));
    }
}
