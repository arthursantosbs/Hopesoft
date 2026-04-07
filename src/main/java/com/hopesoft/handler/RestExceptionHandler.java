package com.hopesoft.handler;

import com.hopesoft.exception.CategoriaNotFoundException;
import com.hopesoft.exception.EmpresaNotFoundException;
import com.hopesoft.exception.EstoqueInsuficienteException;
import com.hopesoft.exception.InvalidCredentialsException;
import com.hopesoft.exception.InvalidPayloadException;
import com.hopesoft.exception.ProdutoNotFoundException;
import com.hopesoft.exception.UsuarioNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            UsuarioNotFoundException.class,
            ProdutoNotFoundException.class,
            CategoriaNotFoundException.class,
            EmpresaNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        return createResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            EstoqueInsuficienteException.class,
            InvalidPayloadException.class
    })
    public ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
        return createResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleUnauthorized(RuntimeException ex, WebRequest request) {
        return createResponseEntity(ex, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<Object> createResponseEntity(Exception ex, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }
}
