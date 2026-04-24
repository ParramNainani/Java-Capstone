package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Result;
import model.ResultDetail;

/**
 * ResultDAO - Data Access Object for Result operations.
 */
public class ResultDAO {

    public boolean insertResult(Result result) {
        String sql = "INSERT INTO results (user_id, quiz_id, score, total_marks) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setInt(1, result.getUserId());
            stmt.setInt(2, result.getNumericQuizId());
            stmt.setInt(3, result.getScore());
            stmt.setInt(4, result.getTotalMarks());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        result.setResultId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting result: " + e.getMessage());
        }
        return false;
    }

    public Result getResultById(int resultId) {
        String sql = "SELECT * FROM results WHERE result_id = ?";
        Result result = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, resultId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = mapResultSetToResult(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving result by ID: " + e.getMessage());
        }
        return result;
    }

    public List<Result> getResultsByUserId(int userId) {
        String sql = "SELECT * FROM results WHERE user_id = ?";
        List<Result> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToResult(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving results by User ID: " + e.getMessage());
        }
        return results;
    }

    public List<Result> getResultsByQuizId(int numericQuizId) {
        String sql = "SELECT * FROM results WHERE quiz_id = ?";
        List<Result> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, numericQuizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToResult(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving results by Quiz ID: " + e.getMessage());
        }
        return results;
    }

    private Result mapResultSetToResult(ResultSet rs) throws SQLException {
        Result result = new Result();
        result.setResultId(rs.getInt("result_id"));
        result.setUserId(rs.getInt("user_id"));
        result.setNumericQuizId(rs.getInt("quiz_id"));
        result.setScore(rs.getInt("score"));
        result.setTotalMarks(rs.getInt("total_marks"));
        return result;
    }

    public int getUniqueStudentCount() {
        String sql = "SELECT COUNT(DISTINCT user_id) FROM results";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving unique student count: " + e.getMessage());
        }
        return 0;
    }

    public List<ResultDetail> getRecentResults(int limit) {
        String sql = "SELECT r.result_id, u.username, q.title, r.score, r.total_marks " +
                     "FROM results r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                     "ORDER BY r.result_id DESC LIMIT ?";
        List<ResultDetail> details = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    details.add(new ResultDetail(
                        rs.getInt("result_id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getInt("score"),
                        rs.getInt("total_marks")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving recent results: " + e.getMessage());
        }
        return details;
    }

    /**
     * Returns [excellent, good, average] counts.
     * Excellent >= 80%, Good 50-79%, Average < 50%
     */
    public int[] getPerformanceBreakdown() {
        int[] counts = {0, 0, 0};
        String sql = "SELECT score, total_marks FROM results";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int score = rs.getInt("score");
                int total = rs.getInt("total_marks");
                if (total == 0) continue;
                double pct = (score * 100.0) / total;
                if (pct >= 80) counts[0]++;
                else if (pct >= 50) counts[1]++;
                else counts[2]++;
            }
        } catch (SQLException e) {
            System.err.println("Error getting performance breakdown: " + e.getMessage());
        }
        return counts;
    }

    public int getCompletedQuizzesCount() {
        String sql = "SELECT COUNT(DISTINCT quiz_id) FROM results";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0;
    }

    public List<model.ResultDetail> getStudentResults(int userId) {
        List<model.ResultDetail> details = new ArrayList<>();
        String sql = "SELECT r.result_id, u.username, q.title, r.score, r.total_marks " +
                     "FROM results r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "JOIN quizzes q ON r.quiz_id = q.numeric_quiz_id " +
                     "WHERE r.user_id = ? ORDER BY r.result_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    details.add(new model.ResultDetail(
                        rs.getInt("result_id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getInt("score"),
                        rs.getInt("total_marks")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student results: " + e.getMessage());
        }
        return details;
    }

    public int getStudentCompletedQuizzesCount(int userId) {
        String sql = "SELECT COUNT(*) FROM results WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0;
    }

    public int getStudentScoresOver90Count(int userId) {
        int count = 0;
        String sql = "SELECT score, total_marks FROM results WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int score = rs.getInt("score");
                    int total = rs.getInt("total_marks");
                    if (total > 0 && (score * 100.0 / total) >= 90.0) {
                        count++;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return count;
    }

    /**
     * Returns an array of 7 ints: quizzes completed per day for the last 7 days.
     * Index 0 = 6 days ago, Index 6 = today.
     */
    public int[] getStudentDailyQuizCounts(int userId) {
        int[] counts = new int[7];
        String sql = "SELECT DATE(taken_at) AS quiz_date, COUNT(*) AS cnt " +
                     "FROM results WHERE user_id = ? AND taken_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                     "GROUP BY DATE(taken_at) ORDER BY quiz_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate("quiz_date");
                    long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(
                        d.toLocalDate(), java.time.LocalDate.now());
                    int idx = 6 - (int) daysDiff;
                    if (idx >= 0 && idx < 7) {
                        counts[idx] = rs.getInt("cnt");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting daily quiz counts: " + e.getMessage());
        }
        return counts;
    }
}
