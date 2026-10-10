package com.lotosia.favorite.exception;

import org.springframework.http.HttpStatus;

/**
 * @author: nijataghayev
 */

public class ConflictException extends BaseException {
    private static final long serialVersionUID = 1L;

    public ConflictException(String message) {
        super(message, "CONFLICT", HttpStatus.CONFLICT);
    }
}
