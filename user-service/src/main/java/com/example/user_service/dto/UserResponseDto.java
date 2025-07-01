package com.example.user_service.dto;

import com.example.user_service.model.Role;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private double wallet;
} 