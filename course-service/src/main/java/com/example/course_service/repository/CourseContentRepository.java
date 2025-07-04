package com.example.course_service.repository;

import com.example.course_service.model.CourseContent;
import com.example.course_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseContentRepository extends JpaRepository<CourseContent, Long> {
    List<CourseContent> findByCourse(Course course);
} 