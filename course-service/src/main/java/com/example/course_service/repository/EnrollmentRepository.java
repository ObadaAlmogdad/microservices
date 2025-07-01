package com.example.course_service.repository;

import com.example.course_service.model.Enrollment;
import com.example.course_service.model.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByLearnerId(Long learnerId);
    List<Enrollment> findByCourseId(Long courseId);
    Optional<Enrollment> findByCourseIdAndLearnerId(Long courseId, Long learnerId);
    List<Enrollment> findByStatus(EnrollmentStatus status);
    List<Enrollment> findByLearnerIdAndStatus(Long learnerId, EnrollmentStatus status);
} 