package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A super-generic exception..
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Some parameters are invalid")
public class WarehouseException extends RuntimeException {

    public WarehouseException(String message) {
        super(message);
    }
}
