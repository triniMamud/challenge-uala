package com.example.demo.java.exception.exceptions;

import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message) {
        super(message);
    }

    @Serial
    private static final long serialVersionUID = -6876745701308382277L;
}
