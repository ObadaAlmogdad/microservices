package com.example.exam_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import com.example.exam_service.config.FeignClientConfig;
import com.example.exam_service.dto.CourseDto;
import com.example.exam_service.dto.ApiResponse;

@FeignClient(
    name = "course-gateway-client",
    url = "${gateway.url}",
    configuration = FeignClientConfig.class
)
public interface CourseClient {
    @GetMapping("/api/courses/{id}")
    ApiResponse<CourseDto> getCourseById(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization);
} 
