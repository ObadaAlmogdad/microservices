package com.example.course_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long courseId;
    
    private Long learnerId;
    
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;
    
    private LocalDateTime completedAt;
    
    private Double progress;
    
    private Double score;
    
    private Double amountPaid;
    
    @PrePersist
    protected void onCreate() {
        completedAt = LocalDateTime.now();
    }
} 