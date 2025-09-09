package org.wildcloud.wildcloud_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wildcloud.wildcloud_backend.exception.custom.*;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CameraNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCameraNotFoundException(CameraNotFoundException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePhoneNumberNotFoundException(PhoneNumberNotFoundException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(EmailTakenException.class)
    public ResponseEntity<Map<String, Object>> handleEmailTaken(EmailTakenException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(CameraNotAssociatedWithUserException.class)
    public ResponseEntity<Map<String, Object>> handleICameraNotAssociatedWithUserException(CameraNotAssociatedWithUserException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyOwnsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyOwnsException(UserAlreadyOwnsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(Map.of(
                        "error", message,
                        "status", status.value(),
                        "timestamp", Instant.now()
                ));
    }
}
