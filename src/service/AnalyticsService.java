package service;

import model.Analytics;
import java.util.List;

/**
 * AnalyticsService - Manages generation of system and quiz statistics.
 */
public class AnalyticsService {

    public Analytics getQuizAnalytics(String quizId, List<Integer> allScores) {
        if (allScores == null || allScores.isEmpty()) {
            return new Analytics(quizId, 0, 0.0, 0);
        }

        int total = allScores.size();
        int sum = 0;
        int highest = 0;

        for (int score : allScores) {
            sum += score;
            if (score > highest) highest = score;
        }

        double avg = (double) sum / total;
        return new Analytics(quizId, total, avg, highest);
    }
}
