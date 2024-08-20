package com.study.spring6restmvc.controllers;

import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
public class ValidationController {

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<List<HashMap<String, String>>> handleJPAViolations(TransactionSystemException e) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.badRequest();

        if (e.getCause() instanceof RollbackException
                && e.getCause().getCause() instanceof ConstraintViolationException ce) {
            var errorMapList = ce.getConstraintViolations().stream()
                    .map(constraintViolation -> {
                        var errorMap = new HashMap<String, String>();
                        errorMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                        return errorMap;
                    }).toList();

            return builder.body(errorMapList);
        }
        return builder.build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<HashMap<String, String>>> handleBindErrors(MethodArgumentNotValidException ex) {

        var errorMapList = ex.getFieldErrors().stream()
                .map(fieldError -> {
                    var errorMap = new HashMap<String, String>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).toList();

        return ResponseEntity.badRequest().body(errorMapList);
    }
}
