package com.example.course_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    private String description;
    
    private BigDecimal price;
    
    private Long trainerId;
    
    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.PENDING;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String category;
    
    private Integer duration; // in hours
    
    private String level; // BEGINNER, INTERMEDIATE, ADVANCED
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 