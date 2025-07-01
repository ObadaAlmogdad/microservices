package com.example.course_service.service;

import com.example.course_service.model.Course;
import com.example.course_service.model.CourseStatus;
import com.example.course_service.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course createCourse(Course course) {
        course.setStatus(CourseStatus.PENDING);
        return courseRepository.save(course);
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public List<Course> findByTrainerId(Long trainerId) {
        return courseRepository.findByTrainerId(trainerId);
    }

    public List<Course> findByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status);
    }

    public List<Course> getApprovedCourses() {
        return courseRepository.findByStatus(CourseStatus.APPROVED);
    }

    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(CourseStatus.PENDING);
    }

    public List<Course> getRejectedCourses() {
        return courseRepository.findByStatus(CourseStatus.REJECTED);
    }

    public List<Course> getPublishedCourses() {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED);
    }

    public Course approveCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setStatus(CourseStatus.APPROVED);
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public Course rejectCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setStatus(CourseStatus.REJECTED);
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public Course publishCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setStatus(CourseStatus.PUBLISHED);
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public Course updateCourse(Long id, Course courseDetails) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setTitle(courseDetails.getTitle());
                    course.setDescription(courseDetails.getDescription());
                    course.setPrice(courseDetails.getPrice());
                    course.setCategory(courseDetails.getCategory());
                    course.setDuration(courseDetails.getDuration());
                    course.setLevel(courseDetails.getLevel());
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> findByCategory(String category) {
        return courseRepository.findByCategory(category);
    }

    public List<Course> findByLevel(String level) {
        return courseRepository.findByLevel(level);
    }
} 