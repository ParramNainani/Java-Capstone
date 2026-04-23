package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Result;

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
}
