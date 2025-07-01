package com.example.course_service.service;

import com.example.course_service.model.Exam;
import com.example.course_service.model.Question;
import com.example.course_service.model.Answer;
import com.example.course_service.repository.ExamRepository;
import com.example.course_service.repository.QuestionRepository;
import com.example.course_service.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    // Exam
    public Exam createExam(Exam exam) { return examRepository.save(exam); }
    public List<Exam> getExamsByCourse(Long courseId) { return examRepository.findByCourseId(courseId); }
    public Optional<Exam> getExam(Long id) { return examRepository.findById(id); }

    // Question
    public Question addQuestion(Question question) { return questionRepository.save(question); }
    public List<Question> getQuestionsByExam(Long examId) { return questionRepository.findByExamId(examId); }

    // Answer
    public Answer submitAnswer(Answer answer) { return answerRepository.save(answer); }
    public List<Answer> getAnswersByQuestion(Long questionId) { return answerRepository.findByQuestionId(questionId); }
    public List<Answer> getAnswersByLearner(Long learnerId) { return answerRepository.findByLearnerId(learnerId); }
    public Optional<Answer> getAnswer(Long id) { return answerRepository.findById(id); }
    public Answer evaluateAnswer(Long answerId, boolean isCorrect, double score) {
        Answer answer = answerRepository.findById(answerId).orElseThrow();
        answer.setIsCorrect(isCorrect);
        answer.setScore(score);
        return answerRepository.save(answer);
    }
} 