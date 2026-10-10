package com.lotosia.favorite.exception;

import org.springframework.http.HttpStatus;

/**
 * @author: nijataghayev
 */

public class BadRequestException extends BaseException {
    private static final long serialVersionUID = 1L;

    public BadRequestException(String message) {
        super(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }
}
