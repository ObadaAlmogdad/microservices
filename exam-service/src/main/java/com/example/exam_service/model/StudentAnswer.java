package com.example.exam_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long questionId;
    private String answer;
    private LocalDateTime submittedAt;
    private String evaluation;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
} 