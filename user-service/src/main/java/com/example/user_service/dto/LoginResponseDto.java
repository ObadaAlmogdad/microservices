package com.example.user_service.dto;

import com.example.user_service.model.Role;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class LoginResponseDto {
    private String token;
    private String email;
    private String fullName;
    private Role role;
    private Long userId;
} 