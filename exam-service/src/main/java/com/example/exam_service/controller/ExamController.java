package com.example.exam_service.controller;

import com.example.exam_service.model.Exam;
import com.example.exam_service.model.Question;
import com.example.exam_service.model.StudentAnswer;
import com.example.exam_service.model.ExamResult;
import com.example.exam_service.service.ExamService;
import com.example.exam_service.repository.ExamResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    @Autowired
    private ExamService examService;

    @Autowired
    private ExamResultRepository examResultRepository;

    private static final Logger logger = LoggerFactory.getLogger(ExamController.class);

    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        if (!hasRole("TRAINER") && !hasRole("ADMIN")) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(examService.createExam(exam));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Exam>> getExamsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(examService.getExamsByCourse(courseId));
    }


    @PostMapping("/{examId}/questions")
    public ResponseEntity<Question> addQuestion(@PathVariable Long examId, @RequestBody Question question) {
        if (!hasRole("TRAINER") && !hasRole("ADMIN")) return ResponseEntity.status(403).build();
        question.setExamId(examId);
        return ResponseEntity.ok(examService.addQuestion(question));
    }

    @GetMapping("/{examId}/questions")
    public ResponseEntity<List<Question>> getQuestionsByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getQuestionsByExam(examId));
    }

    // إرسال إجابة (طالب)
    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<StudentAnswer> submitAnswer(@PathVariable Long questionId, @RequestBody StudentAnswer answer, HttpServletRequest request) {
        if (!hasRole("LEARNER")) return ResponseEntity.status(403).build();
        answer.setQuestionId(questionId);
        Long userId = (Long) request.getAttribute("userId");
        answer.setUserId(userId);
        logger.info("[submitAnswer] Received answer: {} | questionId: {} | userId: {}", answer.getAnswer(), questionId, userId);
        return ResponseEntity.ok(examService.submitAnswer(answer));
    }

    // تقييم إجابة (مدرب)
    @PutMapping("/answers/{answerId}/evaluate")
    public ResponseEntity<StudentAnswer> evaluateAnswer(@PathVariable Long answerId, @RequestBody StudentAnswer eval) {
        if (!hasRole("TRAINER") && !hasRole("ADMIN")) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(examService.evaluateAnswer(answerId, eval));
    }

    // ExamResult APIs (للمدرب والأدمن فقط)
    @GetMapping("/results")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public List<ExamResult> getAllResults() {
        return examResultRepository.findAll();
    }

    @GetMapping("/results/{id}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ExamResult getResult(@PathVariable Long id) {
        return examResultRepository.findById(id).orElse(null);
    }

    @GetMapping("/results/user/{userId}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public List<ExamResult> getResultsByUser(@PathVariable Long userId) {
        return examResultRepository.findByUserId(userId);
    }

    @GetMapping("/results/exam/{examId}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public List<ExamResult> getResultsByExam(@PathVariable Long examId) {
        return examResultRepository.findByExamId(examId);
    }

    @PostMapping("/results")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ExamResult addResult(@RequestBody ExamResult result) {
        return examResultRepository.save(result);
    }

    @PutMapping("/results/{id}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ExamResult updateResult(@PathVariable Long id, @RequestBody ExamResult result) {
        ExamResult existing = examResultRepository.findById(id).orElseThrow();
        existing.setUserId(result.getUserId());
        existing.setExamId(result.getExamId());
        existing.setScore(result.getScore());
        existing.setPassed(result.getPassed());
        existing.setEvaluatedAt(result.getEvaluatedAt());
        return examResultRepository.save(existing);
    }

    @DeleteMapping("/results/{id}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public void deleteResult(@PathVariable Long id) {
        examResultRepository.deleteById(id);
    }

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("Authorities: " + auth.getAuthorities());
        }
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
} 