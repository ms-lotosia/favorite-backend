package com.lotosia.favorite.exception;

import org.springframework.http.HttpStatus;

/**
 * @author: nijataghayev
 */

public class UnauthorizedException extends BaseException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
