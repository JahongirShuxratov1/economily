package org.example.economily.exceptions;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.example.economily.exceptions.ErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorMessageException.class)
    public ResponseEntity<ErrorMessage> handleCustomException(ErrorMessageException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.valueOf(ex.getErrorCode().getStatusCode());
        return ResponseEntity.status(httpStatus).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), ex.getErrorCode().name(), ex.getMessage(), ex.getUserMessage(), request.getDescription(false)));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST) // 400
    public ResponseEntity<ErrorMessage> badRequestException(BadRequestException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), BadRequest.name(), ex.getMessage(), "Bad request", request.getDescription(false)));
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(UNAUTHORIZED) // 401
    public ResponseEntity<?> unauthorizedException(AuthenticationException ex, WebRequest request) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), Unauthorized.name(), ex.getMessage(), null, request.getDescription(false)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN) // 403
    public ResponseEntity<?> accessDeniedException(WebRequest request) {
        return ResponseEntity.status(FORBIDDEN).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), Forbidden.name(), "Forbidden", null, request.getDescription(false)));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(NOT_FOUND) // 404
    public ResponseEntity<?> dataFormatException(WebRequest request) {
        return ResponseEntity.status(NOT_FOUND).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), NotFound.name(), "Not found", null, request.getDescription(false)));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(NOT_FOUND) // 404
    public ResponseEntity<?> noResourceFoundException(WebRequest request) {
        return ResponseEntity.status(NOT_FOUND).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), NotFound.name(), "Not found", null, request.getDescription(false)));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR) // 500
    public ResponseEntity<ErrorMessage> handleGenericException(Exception ex, WebRequest request) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), InternalServerError.name(), ex.getMessage(), "Internal server error", request.getDescription(false)));
    }

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(UNAUTHORIZED) // 401
    public ResponseEntity<?> lockedException(WebRequest request) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), Unauthorized.name(), "Locked exception", null, request.getDescription(false)));
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(FORBIDDEN) // 403
    public ResponseEntity<?> disabledException(WebRequest request) {
        return ResponseEntity.status(FORBIDDEN).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), Forbidden.name(), "Disable exception", null, request.getDescription(false)));
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(UNAUTHORIZED) // 401
    public ResponseEntity<?> badCredentialsException(WebRequest request) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ErrorMessage(new Timestamp(System.currentTimeMillis()), Unauthorized.name(), "Bad credentials exception", null, request.getDescription(false)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String msg = error.getDefaultMessage();
            errors.put(field, msg);
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
