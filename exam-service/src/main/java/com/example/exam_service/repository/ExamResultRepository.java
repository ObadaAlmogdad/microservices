 package com.example.exam_service.repository;

import com.example.exam_service.model.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByUserId(Long userId);
    List<ExamResult> findByExamId(Long examId);
}
