package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Quiz;

/**
 * QuizDAO - Data access for quizzes.
 */
public class QuizDAO {

    public int insertQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes (quiz_id, title, time_limit, creator_id, active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setString(1, quiz.getQuizId()); // The UUID/String ID from frontend
            stmt.setString(2, quiz.getTitle());
            stmt.setInt(3, quiz.getTimeLimit());
            stmt.setInt(4, quiz.getCreatorId());
            stmt.setBoolean(5, quiz.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int numId = generatedKeys.getInt(1);
                        quiz.setNumericQuizId(numId); // update object with assigned numeric auto-increment
                        return numId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting quiz: " + e.getMessage());
        }
        return -1;
    }

    public Quiz getQuizById(int numericQuizId) {
        String sql = "SELECT * FROM quizzes WHERE numeric_quiz_id = ?";
        Quiz quiz = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, numericQuizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    quiz = mapResultSetToQuiz(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving quiz by ID: " + e.getMessage());
        }
        return quiz;
    }

    public List<Quiz> getAllActiveQuizzes() {
        String sql = "SELECT * FROM quizzes WHERE active = true";
        List<Quiz> quizzes = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                quizzes.add(mapResultSetToQuiz(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving active quizzes: " + e.getMessage());
        }
        return quizzes;
    }

    public List<Quiz> getQuizzesByCreator(int creatorId) {
        String sql = "SELECT * FROM quizzes WHERE creator_id = ?";
        List<Quiz> quizzes = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, creatorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapResultSetToQuiz(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving quizzes by creator: " + e.getMessage());
        }
        return quizzes;
    }

    public boolean updateQuiz(Quiz quiz) {
        String sql = "UPDATE quizzes SET quiz_id = ?, title = ?, time_limit = ?, active = ? WHERE numeric_quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, quiz.getQuizId());
            stmt.setString(2, quiz.getTitle());
            stmt.setInt(3, quiz.getTimeLimit());
            stmt.setBoolean(4, quiz.isActive());
            stmt.setInt(5, quiz.getNumericQuizId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating quiz: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteQuiz(int numericQuizId) {
        String sql = "DELETE FROM quizzes WHERE numeric_quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, numericQuizId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting quiz: " + e.getMessage());
            return false;
        }
    }

    private Quiz mapResultSetToQuiz(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setQuizId(rs.getString("quiz_id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setTimeLimit(rs.getInt("time_limit"));
        quiz.setNumericQuizId(rs.getInt("numeric_quiz_id"));
        quiz.setCreatorId(rs.getInt("creator_id"));
        quiz.setActive(rs.getBoolean("active"));
        // Notes: questions list is intentionally skipped here as that usually 
        // implies a secondary query or JOIN with QuestionDAO not handled in this core method.
        return quiz;
    }

    public int getTotalQuizzesCount() {
        String sql = "SELECT COUNT(*) FROM quizzes";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total quizzes count: " + e.getMessage());
        }
        return 0;
    }
}
