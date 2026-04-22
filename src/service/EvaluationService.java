package service;

import model.Response;
import model.Result;
import util.Constants;
import java.util.List;

/**
 * EvaluationService - Handles grading and result generation.
 */
public class EvaluationService {

    public Result evaluateSubmission(String studentId, String quizId, List<Response> responses) {
        int score = 0;
        for (Response r : responses) {
            if (r.isCorrect()) {
                score++;
            }
        }
        
        // Calculate basic grade
        String grade = score >= Constants.PASSING_SCORE ? "PASS" : "FAIL";
        
        return new Result(studentId, quizId, score, grade);
    }
}
