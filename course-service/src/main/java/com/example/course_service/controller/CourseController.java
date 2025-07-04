package com.example.course_service.controller;

import com.example.course_service.model.Course;
import com.example.course_service.model.CourseStatus;
import com.example.course_service.service.CourseService;
import com.example.course_service.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.course_service.dto.CourseContentDto;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(ApiResponse.success("Course created", courseService.createCourse(course)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourse(@PathVariable Long id) {
        logger.info("[getCourse] Requested id: {}", id);
        return courseService.findById(id)
                .map(c -> {
                    logger.info("[getCourse] Found course: {}", c);
                    return ResponseEntity.ok(ApiResponse.success(c));
                })
                .orElseGet(() -> {
                    logger.warn("[getCourse] Course not found for id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.success(courseService.getAll()));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<ApiResponse<List<Course>>> getCoursesByTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.findByTrainerId(trainerId)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Course>>> getCoursesByStatus(@PathVariable CourseStatus status) {
        return ResponseEntity.ok(ApiResponse.success(courseService.findByStatus(status)));
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<Course>>> getApprovedCourses() {
        return ResponseEntity.ok(ApiResponse.success(courseService.getApprovedCourses()));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Course>>> getPendingCourses() {
        return ResponseEntity.ok(ApiResponse.success(courseService.getPendingCourses()));
    }

    @GetMapping("/rejected")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Course>>> getRejectedCourses() {
        return ResponseEntity.ok(ApiResponse.success(courseService.getRejectedCourses()));
    }

//    @GetMapping("/published")
//    public ResponseEntity<List<Course>> getPublishedCourses() {
//        return ResponseEntity.ok(courseService.getPublishedCourses());
//    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Course>> approveCourse(@PathVariable Long id) {
        try {
            Course approvedCourse = courseService.approveCourse(id);
            return ResponseEntity.ok(ApiResponse.success("Course approved", approvedCourse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Course>> rejectCourse(@PathVariable Long id) {
        try {
            Course rejectedCourse = courseService.rejectCourse(id);
            return ResponseEntity.ok(ApiResponse.success("Course rejected", rejectedCourse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<ApiResponse<Course>> publishCourse(@PathVariable Long id) {
        try {
            Course publishedCourse = courseService.publishCourse(id);
            return ResponseEntity.ok(ApiResponse.success("Course published", publishedCourse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {
        try {
            Course updatedCourse = courseService.updateCourse(id, courseDetails);
            return ResponseEntity.ok(ApiResponse.success("Course updated", updatedCourse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok(ApiResponse.success("Course deleted", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Course>>> getCoursesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success(courseService.findByCategory(category)));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<List<Course>>> getCoursesByLevel(@PathVariable String level) {
        return ResponseEntity.ok(ApiResponse.success(courseService.findByLevel(level)));
    }

    // رفع ملف جديد لمحتوى كورس (للمدرب فقط)
    @PostMapping("/{courseId}/contents")
    public CourseContentDto uploadCourseContent(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Long userId
    ) throws Exception {
        // تحقق أن المستخدم هو صاحب الكورس يتم في الخدمة
        return courseService.uploadCourseContent(courseId, file, userId);
    }

    // جلب كل محتويات كورس (متاح للجميع)
    @GetMapping("/{courseId}/contents")
    public List<CourseContentDto> getCourseContents(@PathVariable Long courseId) {
        return courseService.getCourseContents(courseId);
    }
} 