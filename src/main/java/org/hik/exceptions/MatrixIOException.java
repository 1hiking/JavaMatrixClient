package org.hik.exceptions;


public class MatrixIOException extends RuntimeException {
    public MatrixIOException(String message) {
        super(message);
    }

    public MatrixIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
