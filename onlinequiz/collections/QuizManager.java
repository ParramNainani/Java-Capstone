package collections;

import java.util.ArrayList;
import java.util.List;
import model.Quiz;

/**
 * QuizManager - Manages quizzes in the system.
 */
public class QuizManager {
    private List<Quiz> quizzes = new ArrayList<>();

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }
}
