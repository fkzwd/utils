package com.vk.dwzkf.utils.http.query.exception;

public class ParsingFormatException extends Exception {
    public ParsingFormatException() {
    }

    public ParsingFormatException(String message) {
        super(message);
    }

    public ParsingFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingFormatException(Throwable cause) {
        super(cause);
    }
}
