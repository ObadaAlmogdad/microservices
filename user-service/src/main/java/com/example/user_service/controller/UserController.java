package com.example.user_service.controller;

import com.example.user_service.dto.*;
import com.example.user_service.model.User;
import com.example.user_service.model.Role;
import com.example.user_service.service.UserService;
import com.example.user_service.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User user = User.builder()
                .fullName(registrationDto.getFullName())
                .email(registrationDto.getEmail())
                .password(registrationDto.getPassword())
                .role(Role.LEARNER)
                .active(true)
                .wallet(100.0)
                .build();
        
        UserResponseDto createdUser = userService.createUser(user);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", createdUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody UserLoginDto loginDto) {
        User user = userService.findByEmailForAuth(loginDto.getEmail())
                .orElse(null);
        
        if (user != null && userService.validatePassword(loginDto.getPassword(), user.getPassword()) && user.isActive()) {
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
            
            LoginResponseDto response = LoginResponseDto.builder()
                    .token(token)
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .userId(user.getId())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        }
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid credentials or inactive user"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(@PathVariable Long id) {
        logger.info("[getUser] Requested id: {}", id);
        UserResponseDto user = userService.findById(id);
        logger.info("[getUser] Result: {}", user);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAll() {
        List<UserResponseDto> users = userService.getAll();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByEmail(@PathVariable String email) {
        UserResponseDto user = userService.findByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getUsersByRole(@PathVariable Role role) {
        List<UserResponseDto> users = userService.findByRole(role);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/trainers")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllTrainers() {
        List<UserResponseDto> trainers = userService.getAllTrainers();
        return ResponseEntity.ok(ApiResponse.success(trainers));
    }

    @GetMapping("/learners")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllLearners() {
        List<UserResponseDto> learners = userService.getAllLearners();
        return ResponseEntity.ok(ApiResponse.success(learners));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        UserResponseDto updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> activateUser(@PathVariable Long id) {
        UserResponseDto activatedUser = userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", activatedUser));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> deactivateUser(@PathVariable Long id) {
        UserResponseDto deactivatedUser = userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", deactivatedUser));
    }

    @PostMapping("/admin/add-trainer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> addTrainer(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User trainer = User.builder()
                .fullName(registrationDto.getFullName())
                .email(registrationDto.getEmail())
                .password(registrationDto.getPassword())
                .role(Role.TRAINER)
                .active(true)
                .wallet(100.0)
                .build();
        
        UserResponseDto createdTrainer = userService.createUser(trainer);
        return ResponseEntity.ok(ApiResponse.success("Trainer added successfully", createdTrainer));
    }

    @PostMapping("/admin/add-admin")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> addAdmin(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User admin = User.builder()
                .fullName(registrationDto.getFullName())
                .email(registrationDto.getEmail())
                .password(registrationDto.getPassword())
                .role(Role.ADMIN)
                .active(true)
                .wallet(100.0)
                .build();
        
        UserResponseDto createdAdmin = userService.createUser(admin);
        return ResponseEntity.ok(ApiResponse.success("Admin added successfully", createdAdmin));
    }

    @PutMapping("/{id}/wallet")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateWallet(@PathVariable Long id, @RequestParam double amount) {
        UserResponseDto user = userService.updateWallet(id, amount);
        return ResponseEntity.ok(ApiResponse.success("Wallet updated successfully", user));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponseDto user = userService.findById(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}