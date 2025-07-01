package com.example.course_service.service;

import com.example.course_service.model.Enrollment;
import com.example.course_service.model.EnrollmentStatus;
import com.example.course_service.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import com.example.course_service.model.Course;
import com.example.course_service.dto.UserDto;
import com.example.course_service.client.PaymentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    @Autowired
    private PaymentClient paymentClient;

    public Enrollment enrollInCourse(Long courseId, Long learnerId, String jwtToken) {
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByCourseIdAndLearnerId(courseId, learnerId);
        if (existingEnrollment.isPresent()) {
            throw new RuntimeException("Already enrolled in this course");
        }
        RestTemplate restTemplate = new RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
        org.springframework.http.ResponseEntity<com.example.course_service.dto.ApiResponse> courseResponse = restTemplate.exchange(
            "http://localhost:8082/api/courses/" + courseId,
            org.springframework.http.HttpMethod.GET,
            entity,
            com.example.course_service.dto.ApiResponse.class
        );
        com.example.course_service.dto.ApiResponse apiResponse = courseResponse.getBody();
        Object data = apiResponse != null ? apiResponse.getData() : null;
        // تحويل data إلى Course
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Course course = mapper.convertValue(data, Course.class);
        if (course == null || course.getPrice() == null) throw new RuntimeException("Course or price is not set");
        BigDecimal price = course.getPrice();
        // استدعاء خدمة الدفع
        ResponseEntity<?> paymentResponse = paymentClient.pay(learnerId, price.doubleValue(), "Course Enrollment: " + course.getTitle(), "Bearer " + jwtToken);
        if (!paymentResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Payment failed");
        }
        Enrollment enrollment = Enrollment.builder()
                .courseId(courseId)
                .learnerId(learnerId)
                .status(EnrollmentStatus.ACTIVE)
                .progress(0.0)
                .amountPaid(price.doubleValue())
                .build();
        return enrollmentRepository.save(enrollment);
    }

    public Optional<Enrollment> findById(Long id) {
        return enrollmentRepository.findById(id);
    }

    public List<Enrollment> findByLearnerId(Long learnerId) {
        return enrollmentRepository.findByLearnerId(learnerId);
    }

    public List<Enrollment> findByCourseId(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public Optional<Enrollment> findByCourseIdAndLearnerId(Long courseId, Long learnerId) {
        return enrollmentRepository.findByCourseIdAndLearnerId(courseId, learnerId);
    }

    public Enrollment updateProgress(Long enrollmentId, Double progress) {
        return enrollmentRepository.findById(enrollmentId)
                .map(enrollment -> {
                    enrollment.setProgress(progress);
                    if (progress >= 100.0) {
                        enrollment.setStatus(EnrollmentStatus.COMPLETED);
                        enrollment.setCompletedAt(LocalDateTime.now());
                    }
                    return enrollmentRepository.save(enrollment);
                })
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));
    }

    public Enrollment completeCourse(Long enrollmentId, Double score) {
        return enrollmentRepository.findById(enrollmentId)
                .map(enrollment -> {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    enrollment.setProgress(100.0);
                    enrollment.setScore(score);
                    enrollment.setCompletedAt(LocalDateTime.now());
                    return enrollmentRepository.save(enrollment);
                })
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));
    }

    public Enrollment dropCourse(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .map(enrollment -> {
                    enrollment.setStatus(EnrollmentStatus.DROPPED);
                    return enrollmentRepository.save(enrollment);
                })
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));
    }

    public List<Enrollment> getActiveEnrollments(Long learnerId) {
        return enrollmentRepository.findByLearnerIdAndStatus(learnerId, EnrollmentStatus.ACTIVE);
    }

    public List<Enrollment> getCompletedEnrollments(Long learnerId) {
        return enrollmentRepository.findByLearnerIdAndStatus(learnerId, EnrollmentStatus.COMPLETED);
    }
} 