package com.example.exam_service.exception;

import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> handleFeignNotFound(FeignException.NotFound ex) {
        return ResponseEntity.status(404).body("Course not found (404 from course-service)");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOther(Exception ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
} 