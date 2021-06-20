package com.vk.dwzkf.utils.http.query.exception;

public class QueryValidationException extends RuntimeException {
    public QueryValidationException() {
    }

    public QueryValidationException(String message) {
        super(message);
    }

    public QueryValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryValidationException(Throwable cause) {
        super(cause);
    }
}
