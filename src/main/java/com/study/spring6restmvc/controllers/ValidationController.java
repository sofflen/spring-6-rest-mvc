package com.study.spring6restmvc.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class ValidationController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<HashMap<String, String>>> handleMethodArgumentNotValidException
            (MethodArgumentNotValidException ex) {

        var errorMapList = ex.getFieldErrors().stream()
                .map(fieldError -> {
                    var errorMap = new HashMap<String, String>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).toList();

        return ResponseEntity.badRequest().body(errorMapList);
    }
}
