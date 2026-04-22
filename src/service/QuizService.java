package service;

import model.MCQQuestion;
import model.Question;
import model.Quiz;
import model.Result;
import model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizService {

    public List<Quiz> getAvailableQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();

        List<Question<?>> javaQuestions = new ArrayList<>();
        
        Map<String, String> q1Options = new HashMap<>();
        q1Options.put("A", "Java Virtual Machine");
        q1Options.put("B", "Java Vendor Machine");
        q1Options.put("C", "Joint Virtual Method");
        javaQuestions.add(new MCQQuestion(1, "What is JVM?", q1Options, "A"));
        
        Map<String, String> q2Options = new HashMap<>();
        q2Options.put("A", "implement");
        q2Options.put("B", "extends");
        q2Options.put("C", "inherits");
        javaQuestions.add(new MCQQuestion(2, "Which keyword is used for inheritance?", q2Options, "B"));

        Quiz javaQuiz = new Quiz("QZ-001", "Java Basics Quiz", javaQuestions, 15);
        quizzes.add(javaQuiz);
        return quizzes;
    }

    public Quiz getQuizById(String quizId) {
        return null;
    }

    public boolean addQuiz(Quiz quiz) {
        return quiz != null;
    }

    public Result evaluateQuiz(Student student, Quiz quiz, Map<Integer, String> selectedAnswers) {
        int correct = 0;
        int wrong = 0;

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question<?> q = quiz.getQuestions().get(i);
            String chosen = selectedAnswers.get(q.getQuestionId());

            if (chosen != null && q.getCorrectAnswer().toString().equalsIgnoreCase(chosen)) {
                correct++;
            } else {
                wrong++;
            }
        }

        String grade = correct > 1 ? "PASS" : "FAIL";
        return new Result(student != null ? student.getStudentId() : "Unknown", quiz.getQuizId(), correct, grade);
    }

    public void submitAttemptToBackend(Student student, Quiz quiz, Map<Integer, String> answers) {
        // integration later
    }
}