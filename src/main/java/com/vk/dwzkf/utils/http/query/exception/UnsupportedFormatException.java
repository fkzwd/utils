package com.vk.dwzkf.utils.http.query.exception;

public class UnsupportedFormatException extends RuntimeException {
    public UnsupportedFormatException() {
    }

    public UnsupportedFormatException(String message) {
        super(message);
    }

    public UnsupportedFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedFormatException(Throwable cause) {
        super(cause);
    }
}
