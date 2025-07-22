package org.example.economily.exceptions;


import java.sql.Timestamp;

public record ErrorMessage(
        Timestamp timestamp,
        String errorCode,
        String message,
        String userMessage,
        String path
) {
}
