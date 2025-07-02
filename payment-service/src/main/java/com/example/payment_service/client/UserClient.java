package com.example.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.stereotype.Component;
import com.example.payment_service.dto.UserDto;
import com.example.payment_service.config.FeignClientConfig;
import com.example.payment_service.model.Role;
import com.example.payment_service.dto.ApiResponse;

@FeignClient(
    name = "api-gateway",
    url = "${gateway.url}",
    fallback = UserClientFallback.class,
    configuration = FeignClientConfig.class
)
public interface UserClient {
    @GetMapping("/api/users/{id}")
    ApiResponse<UserDto> getUserById(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization);

    @PutMapping("/api/users/{id}/wallet")
    UserDto updateWallet(@PathVariable("id") Long id, @RequestParam("amount") double amount, @RequestHeader("Authorization") String authorization);
}

@Component
class UserClientFallback implements UserClient {
    @Override
    public ApiResponse<UserDto> getUserById(Long id, String authorization) {
        System.out.println("[UserClientFallback] getUserById fallback activated!");
        return ApiResponse.error("User not found");
    }

    @Override
    public UserDto updateWallet(Long id, double amount, String authorization) {
        return null; // Implementation needed
    }
} 