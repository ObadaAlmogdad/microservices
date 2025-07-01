package com.example.exam_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long examId;

    @Lob
    private String text;

    @Enumerated(EnumType.STRING)
    private QuestionType type; // MCQ, TEXT, TRUE_FALSE

    @Lob
    private String options; // JSON إذا كان MCQ

    private String correctAnswer;
} 