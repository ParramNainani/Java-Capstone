package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AnalyticsDAO - Data Access Object for dashboard analytics and aggregations.
 */
public class AnalyticsDAO {

    public int getTotalStudentCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'STUDENT'"; // Alternatively: UPPER(role) = 'STUDENT'
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total student count: " + e.getMessage());
        }
        return count;
    }

    public int getTotalQuizAttempts(int quizId) {
        String sql = "SELECT COUNT(*) FROM results WHERE quiz_id = ?";
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total quiz attempts: " + e.getMessage());
        }
        return count;
    }

    public double getAverageScoreForQuiz(int quizId) {
        String sql = "SELECT AVG(score) FROM results WHERE quiz_id = ?";
        double average = 0.0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    average = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving average score for quiz: " + e.getMessage());
        }
        return average;
    }

    public int getHighestScoreForQuiz(int quizId) {
        String sql = "SELECT MAX(score) FROM results WHERE quiz_id = ?";
        int maxScore = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    maxScore = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving highest score for quiz: " + e.getMessage());
        }
        return maxScore;
    }
}
