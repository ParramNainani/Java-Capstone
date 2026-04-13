package collections;

import java.util.ArrayList;
import java.util.List;
import model.Question;

/**
 * QuestionBank - Manages the collection of questions.
 */
public class QuestionBank {
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question q) {
        questions.add(q);
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
