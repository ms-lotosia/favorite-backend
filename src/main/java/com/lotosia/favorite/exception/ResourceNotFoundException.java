package com.lotosia.favorite.exception;

import org.springframework.http.HttpStatus;

/**
 * @author: nijataghayev
 */

public class ResourceNotFoundException extends BaseException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message, "NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
