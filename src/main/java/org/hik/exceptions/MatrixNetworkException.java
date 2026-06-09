package org.hik.exceptions;


public class MatrixNetworkException extends RuntimeException {
    public MatrixNetworkException(String message) {
        super(message);
    }

    public MatrixNetworkException(String message, Throwable cause) {
        super(message, cause);
    }


}
