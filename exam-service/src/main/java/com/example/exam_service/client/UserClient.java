package com.example.exam_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import com.example.exam_service.config.FeignClientConfig;
import org.springframework.stereotype.Component;

@FeignClient(
    name = "user-gateway-client",
    url = "${gateway.url}",
    configuration = FeignClientConfig.class
)
public interface UserClient {
    @GetMapping("/api/users/{id}")
    Object getUserById(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization);
} 