package com.example.exam_service.repository;

import com.example.exam_service.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByUserIdAndQuestionId(Long userId, Long questionId);
} 