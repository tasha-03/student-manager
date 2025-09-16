package com.tasha.socialinfo.security;

import com.tasha.socialinfo.exception.GlobalExceptionHandler;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class SecurityExceptionHandler {
    public record ApiErrorResponse(
            int status,
            String error,
            String message,
            String path,
            LocalDateTime timestamp
    ) {}

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            WebRequest request) {

        GlobalExceptionHandler.ApiErrorResponse response = new GlobalExceptionHandler.ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> handleUserNotFound(
            UsernameNotFoundException ex,
            WebRequest request) {

        GlobalExceptionHandler.ApiErrorResponse response = new GlobalExceptionHandler.ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            WebRequest request) {

        GlobalExceptionHandler.ApiErrorResponse response = new GlobalExceptionHandler.ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "You do not have permission to access this resource",
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> handleExpiredJwt(
            ExpiredJwtException ex,
            WebRequest request) {

        GlobalExceptionHandler.ApiErrorResponse response = new GlobalExceptionHandler.ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "JWT token was expired",
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({MalformedJwtException.class, SignatureException.class})
    public ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> handleExpiredJwt(
            Exception ex,
            WebRequest request) {

        GlobalExceptionHandler.ApiErrorResponse response = new GlobalExceptionHandler.ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Invalid JWT token",
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
