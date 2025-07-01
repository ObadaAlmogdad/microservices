package com.example.course_service.controller;

import com.example.course_service.model.Exam;
import com.example.course_service.model.Question;
import com.example.course_service.model.Answer;
import com.example.course_service.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    @Autowired
    private ExamService examService;

    // إضافة امتحان (فقط للمدرب)
    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam, @RequestHeader("Role") String role) {
        if (!"TRAINER".equals(role) && !"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(examService.createExam(exam));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Exam>> getExamsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(examService.getExamsByCourse(courseId));
    }

    // إضافة سؤال (فقط للمدرب)
    @PostMapping("/{examId}/questions")
    public ResponseEntity<Question> addQuestion(@PathVariable Long examId, @RequestBody Question question, @RequestHeader("Role") String role) {
        if (!"TRAINER".equals(role) && !"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        question.setExamId(examId);
        return ResponseEntity.ok(examService.addQuestion(question));
    }

    @GetMapping("/{examId}/questions")
    public ResponseEntity<List<Question>> getQuestionsByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getQuestionsByExam(examId));
    }

    // إرسال إجابة (طالب)
    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<Answer> submitAnswer(@PathVariable Long questionId, @RequestBody Answer answer, @RequestHeader("Role") String role) {
        if (!"LEARNER".equals(role)) return ResponseEntity.status(403).build();
        answer.setQuestionId(questionId);
        return ResponseEntity.ok(examService.submitAnswer(answer));
    }

    // تقييم إجابة (مدرب)
    @PutMapping("/answers/{answerId}/evaluate")
    public ResponseEntity<Answer> evaluateAnswer(@PathVariable Long answerId, @RequestBody Answer eval, @RequestHeader("Role") String role) {
        if (!"TRAINER".equals(role) && !"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(examService.evaluateAnswer(answerId, eval.getIsCorrect(), eval.getScore()));
    }
} 