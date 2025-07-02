package com.example.exam_service.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
} 