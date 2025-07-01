package com.example.course_service.controller;

import com.example.course_service.model.Enrollment;
import com.example.course_service.service.EnrollmentService;
import com.example.course_service.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    @Autowired
    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<Enrollment>> enrollInCourse(@RequestParam Long courseId, @RequestParam Long learnerId, @RequestHeader("Authorization") String authorization) {
        try {
            String jwtToken = authorization.replace("Bearer ", "");
            Enrollment enrollment = enrollmentService.enrollInCourse(courseId, learnerId, jwtToken);
            return ResponseEntity.ok(ApiResponse.success("Enrolled and paid successfully", enrollment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Enrollment>> getEnrollment(@PathVariable Long id) {
        return enrollmentService.findById(id)
                .map(e -> ResponseEntity.ok(ApiResponse.success(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/learner/{learnerId}")
    public ResponseEntity<ApiResponse<List<Enrollment>>> getEnrollmentsByLearner(@PathVariable Long learnerId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.findByLearnerId(learnerId)));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Enrollment>>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.findByCourseId(courseId)));
    }

    @GetMapping("/learner/{learnerId}/course/{courseId}")
    public ResponseEntity<ApiResponse<Enrollment>> getEnrollmentByLearnerAndCourse(@PathVariable Long learnerId, @PathVariable Long courseId) {
        return enrollmentService.findByCourseIdAndLearnerId(courseId, learnerId)
                .map(e -> ResponseEntity.ok(ApiResponse.success(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<Enrollment>> updateProgress(@PathVariable Long id, @RequestParam Double progress) {
        try {
            Enrollment updatedEnrollment = enrollmentService.updateProgress(id, progress);
            return ResponseEntity.ok(ApiResponse.success(updatedEnrollment));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Enrollment>> completeCourse(@PathVariable Long id, @RequestParam Double score) {
        try {
            Enrollment completedEnrollment = enrollmentService.completeCourse(id, score);
            return ResponseEntity.ok(ApiResponse.success(completedEnrollment));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/drop")
    public ResponseEntity<ApiResponse<Enrollment>> dropCourse(@PathVariable Long id) {
        try {
            Enrollment droppedEnrollment = enrollmentService.dropCourse(id);
            return ResponseEntity.ok(ApiResponse.success(droppedEnrollment));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/learner/{learnerId}/active")
    public ResponseEntity<ApiResponse<List<Enrollment>>> getActiveEnrollments(@PathVariable Long learnerId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.getActiveEnrollments(learnerId)));
    }

    @GetMapping("/learner/{learnerId}/completed")
    public ResponseEntity<ApiResponse<List<Enrollment>>> getCompletedEnrollments(@PathVariable Long learnerId) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.getCompletedEnrollments(learnerId)));
    }
} 