package ru.uchebka.config;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException e) {
        log.warn("Request validation failed: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("message", "Проверьте материал и параметры теста."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> general(Exception e) {
        // Keep the client response generic, but preserve the real cause in Docker logs.
        log.error("Request processing failed: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", "Не удалось обработать запрос. Проверьте настройки AI-сервиса и повторите попытку."));
    }
}
