package com.example.course_service.service;

import com.example.course_service.model.Course;
import com.example.course_service.model.CourseStatus;
import com.example.course_service.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.course_service.dto.CourseContentDto;
import com.example.course_service.model.CourseContent;
import com.example.course_service.repository.CourseContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseContentRepository courseContentRepository;

    @Value("${course.content.upload-dir:uploads/}")
    private String uploadDir;

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

    @Transactional
    public CourseContentDto uploadCourseContent(Long courseId, MultipartFile file, Long uploaderId) throws Exception {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    
        if (!course.getTrainerId().equals(uploaderId)) {
            throw new RuntimeException("You are not the owner of this course");
        }
      
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // حفظ الملف
        String originalFileName = file.getOriginalFilename();
        String storedFileName = System.currentTimeMillis() + "_" + originalFileName;
        Path filePath = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), filePath);

        // حفظ البيانات في قاعدة البيانات
        CourseContent content = new CourseContent();
        content.setCourse(course);
        content.setFileName(originalFileName);
        content.setFilePath(storedFileName);
        content.setUploadDate(LocalDateTime.now());
        content.setUploaderId(uploaderId);
        courseContentRepository.save(content);

        // إعداد DTO
        CourseContentDto dto = new CourseContentDto();
        dto.setId(content.getId());
        dto.setFileName(originalFileName);
        dto.setFileUrl("/uploads/" + storedFileName);
        dto.setUploadDate(content.getUploadDate());
        dto.setUploaderId(uploaderId);
        return dto;
    }

    public List<CourseContentDto> getCourseContents(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<CourseContent> contents = courseContentRepository.findByCourse(course);
        return contents.stream().map(content -> {
            CourseContentDto dto = new CourseContentDto();
            dto.setId(content.getId());
            dto.setFileName(content.getFileName());
            dto.setFileUrl("/uploads/" + content.getFilePath());
            dto.setUploadDate(content.getUploadDate());
            dto.setUploaderId(content.getUploaderId());
            return dto;
        }).collect(Collectors.toList());
    }
} 