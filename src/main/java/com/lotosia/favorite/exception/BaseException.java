package com.lotosia.favorite.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author: nijataghayev
 */

@Getter
public abstract class BaseException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    protected BaseException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
