package service;

import model.Analytics;
import model.Result;

import java.util.*;

/**
 * Handles analytics calculations
 */
public class AnalyticsService {

    // Existing logic
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

    // Leaderboard
    public List<Result> getLeaderboard(List<Result> results) {
        if (results == null) return new ArrayList<>();

        results.sort((r1, r2) -> Double.compare(r2.getPercentage(), r1.getPercentage()));
        return results;
    }

    // Pass/Fail stats
    public Map<String, Integer> getPassFailStats(List<Result> results) {
        int pass = 0, fail = 0;

        for (Result r : results) {
            if (r.getPercentage() >= 40) pass++;
            else fail++;
        }

        Map<String, Integer> stats = new HashMap<>();
        stats.put("Pass", pass);
        stats.put("Fail", fail);

        return stats;
    }
}