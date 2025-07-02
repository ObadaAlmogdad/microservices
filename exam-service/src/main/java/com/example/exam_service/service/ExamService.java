package com.example.exam_service.service;

import com.example.exam_service.model.Exam;
import com.example.exam_service.model.Question;
import com.example.exam_service.model.StudentAnswer;
import com.example.exam_service.repository.ExamRepository;
import com.example.exam_service.repository.QuestionRepository;
import com.example.exam_service.repository.StudentAnswerRepository;
import com.example.exam_service.client.UserClient;
import com.example.exam_service.client.CourseClient;
import com.example.exam_service.dto.CourseDto;
import com.example.exam_service.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import feign.FeignException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final UserClient userClient;
    private final CourseClient courseClient;

    // Exam
    public Exam createExam(Exam exam) {
        ApiResponse<CourseDto> response = courseClient.getCourseById(exam.getCourseId(), getAuthHeader());
        CourseDto course = response != null ? response.getData() : null;
        if (course == null) throw new RuntimeException("Course not found");
        return examRepository.save(exam);
    }
    public List<Exam> getExamsByCourse(Long courseId) { return examRepository.findAll().stream().filter(e -> e.getCourseId().equals(courseId)).toList(); }
    public Optional<Exam> getExam(Long id) { return examRepository.findById(id); }

    // Question
    public Question addQuestion(Question question) { return questionRepository.save(question); }
    public List<Question> getQuestionsByExam(Long examId) { return questionRepository.findByExamId(examId); }

    // StudentAnswer
    public StudentAnswer submitAnswer(StudentAnswer answer) {
        // تحقق من وجود المستخدم
        Object user = userClient.getUserById(answer.getUserId(), getAuthHeader());
        if (user == null) throw new RuntimeException("User not found");
        return studentAnswerRepository.save(answer);
    }
    public List<StudentAnswer> getAnswersByQuestion(Long questionId) { return studentAnswerRepository.findAll().stream().filter(a -> a.getQuestionId().equals(questionId)).toList(); }
    public List<StudentAnswer> getAnswersByUser(Long userId) { return studentAnswerRepository.findAll().stream().filter(a -> a.getUserId().equals(userId)).toList(); }
    public Optional<StudentAnswer> getAnswer(Long id) { return studentAnswerRepository.findById(id); }
    public StudentAnswer evaluateAnswer(Long answerId, StudentAnswer eval) {
        StudentAnswer answer = studentAnswerRepository.findById(answerId).orElseThrow();
        answer.setEvaluation(eval.getEvaluation()); // عدل فقط التقييم
        // يمكن إضافة منطق تقييم إضافي هنا
        return studentAnswerRepository.save(answer);
    }

    private String getAuthHeader() {
        // استخراج Authorization header من السياق الحالي
        org.springframework.web.context.request.ServletRequestAttributes attr =
            (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            return attr.getRequest().getHeader("Authorization");
        }
        return null;
    }
} 