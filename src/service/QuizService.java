package service;

import model.QuestionModel;
import model.Quiz;
import model.Result;
import model.Student;
import dao.QuizDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizService {

    private final QuizDAO quizDAO = new QuizDAO();

    public List<Quiz> getAvailableQuizzes() {
        return quizDAO.getAllActiveQuizzes();
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
            QuestionModel q = quiz.getQuestions().get(i);
            String chosen = selectedAnswers.get(q.getId());

            if (chosen != null && q.getCorrectAnswer().equalsIgnoreCase(chosen)) {
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