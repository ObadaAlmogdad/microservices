package com.example.exam_service.repository;

import com.example.exam_service.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {
} 