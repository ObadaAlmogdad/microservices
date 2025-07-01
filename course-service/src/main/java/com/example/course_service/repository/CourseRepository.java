package com.example.course_service.repository;

import com.example.course_service.model.Course;
import com.example.course_service.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTrainerId(Long trainerId);
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByTrainerIdAndStatus(Long trainerId, CourseStatus status);
    List<Course> findByCategory(String category);
    List<Course> findByLevel(String level);
} 