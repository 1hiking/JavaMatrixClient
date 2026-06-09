package org.hik.utils;

public enum MatrixErrors {
    M_UNRECOGNIZED(400),
    M_FORBIDDEN(403),
    M_UNKNOWN_TOKEN(401),
    M_BAD_JSON(400),
    M_NOT_FOUND(404),
    M_LIMIT_EXCEEDED(429);

    private final int httpStatusCode;

    MatrixErrors(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }
}