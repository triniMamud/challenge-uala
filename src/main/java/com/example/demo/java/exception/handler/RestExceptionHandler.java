/* Copyright 2020 the original author or authors. All rights reserved. */
package com.example.demo.java.exception.handler;

import com.example.demo.java.exception.ApiError;
import com.example.demo.java.exception.ApiSubError;
import com.example.demo.java.exception.exceptions.ItemNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    protected ResponseEntity<Object> handleItemNotFoundException(Exception ex, WebRequest request) {
        return notFound(ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return badRequest(ex, headers, request);
    }

    private ResponseEntity<Object> notFound(Exception ex, WebRequest request) {
        var apiError = ApiError.builder().status(NOT_FOUND).message(ex.getMessage()).build();

        return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

    private ResponseEntity<Object> badRequest(MethodArgumentNotValidException ex, HttpHeaders headers, WebRequest request) {
        var subErrors = ex.getBindingResult().getFieldErrors().stream().map(error -> ApiSubError.builder()
                .object(error.getObjectName())
                .field(error.getField())
                .message(error.getDefaultMessage())
                .build()
        ).toList();
        var errorDetails = ApiError.builder().status(BAD_REQUEST).message("Validation Error").subErrors(subErrors).build();
        return handleExceptionInternal(ex, errorDetails, headers, errorDetails.getStatus(), request);
    }

}
