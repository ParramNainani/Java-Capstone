package service;

import model.Question;
import model.Quiz;
import model.Result;
import model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizService {

    public List<Quiz> getAvailableQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();

        List<Question> javaQuestions = new ArrayList<>();
        javaQuestions.add(new Question("What is JVM?",
                List.of("Java Virtual Machine", "Java Vendor Machine", "Joint Virtual Method", "None"),
                "Java Virtual Machine"));

        javaQuestions.add(new Question("Which keyword is used for inheritance?",
                List.of("implement", "extends", "inherits", "super"),
                "extends"));

        javaQuestions.add(new Question("Which collection does not allow duplicates?",
                List.of("List", "ArrayList", "Set", "Vector"),
                "Set"));

        Quiz javaQuiz = new Quiz(
                "Java Basics Quiz",
                "Programming",
                15,
                3,
                10,
                javaQuestions
        );

        quizzes.add(javaQuiz);
        return quizzes;
    }

    public Result evaluateQuiz(Student student, Quiz quiz, Map<Integer, String> selectedAnswers) {
        int correct = 0;
        int wrong = 0;

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question q = quiz.getQuestions().get(i);
            String chosen = selectedAnswers.get(i);

            if (chosen != null && chosen.equals(q.getCorrectAnswer())) {
                correct++;
            } else {
                wrong++;
            }
        }

        int score = correct;
        boolean passed = score >= quiz.getPassingMarks();

        return new Result(student, quiz, score, correct, wrong, passed);
    }

    public void submitAttemptToBackend(Student student, Quiz quiz, Map<Integer, String> answers) {
        // TODO:
        // Replace this with JDBC/API integration later
        // Example:
        // responseDAO.saveResponses(student, quiz, answers);
    }
}