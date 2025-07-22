package org.example.economily.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public class HttpStatusException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7034897190745766939L;
    private final String statusMessage;
    private final int statusCode;

    public HttpStatusException(int statusCode, String statusMessage, Throwable cause) {
        super(statusCode + " - " + statusMessage, cause);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}
