package com.hopesoft.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + error.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));

        return buildError(HttpStatus.BAD_REQUEST, "validation_error", message, request);
    }

    @ExceptionHandler({
            ProdutoNotFoundException.class,
            CategoriaNotFoundException.class,
            EmpresaNotFoundException.class,
            UsuarioNotFoundException.class,
            ValeTrocaNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.NOT_FOUND, "not_found", exception.getMessage(), request);
    }

    @ExceptionHandler({
            EstoqueInsuficienteException.class,
            InvalidPayloadException.class,
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            Exception exception,
            HttpServletRequest request
    ) {
        String message = exception instanceof HttpMessageNotReadableException
                ? "Requisicao invalida ou enum fora do formato esperado"
                : exception.getMessage();
        return buildError(HttpStatus.BAD_REQUEST, "bad_request", message, request);
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            InvalidCredentialsException.class
    })
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.UNAUTHORIZED, "invalid_credentials", "Email ou senha invalidos", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            AuthenticationException exception,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.UNAUTHORIZED, "unauthorized", exception.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "internal_error",
                "Erro interno ao processar a requisicao",
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> buildError(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {
        if (status.is5xxServerError()) {
            logger.error("Erro interno [{}]: {} no endpoint {}", code, message, request.getRequestURI());
        } else {
            logger.warn("Erro na requisicao [{}]: {} no endpoint {}", code, message, request.getRequestURI());
        }
        
        return ResponseEntity.status(status).body(
                ApiErrorResponse.of(status, code, message, request.getRequestURI())
        );
    }
}
