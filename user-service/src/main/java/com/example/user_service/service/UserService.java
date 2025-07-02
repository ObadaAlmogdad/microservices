package com.example.user_service.service;

import com.example.user_service.dto.UserResponseDto;
import com.example.user_service.exception.EmailAlreadyExistsException;
import com.example.user_service.exception.UserNotFoundException;
import com.example.user_service.model.User;
import com.example.user_service.model.Role;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto createUser(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role to LEARNER if not specified
        if (user.getRole() == null) {
            user.setRole(Role.LEARNER);
        }
        
        User savedUser = userRepository.save(user);
        return convertToResponseDto(savedUser);
    }

    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return convertToResponseDto(user);
    }

    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<User> findByEmailForAuth(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return convertToResponseDto(user);
    }

    public List<UserResponseDto> findByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> getAllTrainers() {
        return userRepository.findByRole(Role.TRAINER).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> getAllLearners() {
        return userRepository.findByRole(Role.LEARNER).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(userDetails.getEmail());
        }
        
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        
        // Only encode password if it's being changed
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        // Only admin can change roles
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
        
        user.setActive(userDetails.isActive());
        User updatedUser = userRepository.save(user);
        return convertToResponseDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public UserResponseDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(true);
        User activatedUser = userRepository.save(user);
        return convertToResponseDto(activatedUser);
    }

    public UserResponseDto deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(false);
        User deactivatedUser = userRepository.save(user);
        return convertToResponseDto(deactivatedUser);
    }

    public UserResponseDto updateWallet(Long id, double amount) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        double newBalance = user.getWallet() + amount;
        if (newBalance < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        user.setWallet(newBalance);
        User updatedUser = userRepository.save(user);
        return convertToResponseDto(updatedUser);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private UserResponseDto convertToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .wallet(user.getWallet())
                .build();
    }
}

