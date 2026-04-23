package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Question;

/**
 * QuestionDAO - Data Access Object for Question operations.
 * Note: Question<T> is abstract, so instantiation in get methods 
 * utilizes an anonymous class wrapper for DB retrieval.
 */
public class QuestionDAO {

    public int insertQuestion(Question<?> question) {
        String sql = "INSERT INTO questions (quiz_id, question_text, correct_answer, question_type, marks, difficulty_level) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getQuestionText());
            // Safely map proper correct answer to a string suitable for DB based on T
            stmt.setString(3, question.getCorrectAnswer() != null ? question.getCorrectAnswer().toString() : null);
            stmt.setString(4, question.getQuestionType());
            stmt.setInt(5, question.getMarks());
            stmt.setString(6, question.getDifficultyLevel());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        question.setQuestionId(newId);
                        return newId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting question: " + e.getMessage());
        }
        return -1;
    }

    public Question<?> getQuestionById(int questionId) {
        String sql = "SELECT * FROM questions WHERE question_id = ?";
        Question<?> question = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    question = mapResultSetToQuestion(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving question by ID: " + e.getMessage());
        }
        return question;
    }

    public List<Question<?>> getQuestionsByQuizId(int quizId) {
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";
        List<Question<?>> questions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestion(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving questions by Quiz ID: " + e.getMessage());
        }
        return questions;
    }

    public boolean updateQuestion(Question<?> question) {
        String sql = "UPDATE questions SET quiz_id = ?, question_text = ?, correct_answer = ?, question_type = ?, marks = ?, difficulty_level = ? WHERE question_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getQuestionText());
            stmt.setString(3, question.getCorrectAnswer() != null ? question.getCorrectAnswer().toString() : null);
            stmt.setString(4, question.getQuestionType());
            stmt.setInt(5, question.getMarks());
            stmt.setString(6, question.getDifficultyLevel());
            stmt.setInt(7, question.getQuestionId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating question: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, questionId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting question: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper to map result set into the abstract Question form. 
     */
    private Question<String> mapResultSetToQuestion(ResultSet rs) throws SQLException {
        // Instantiate using an anonymous subclass since Question is abstract
        Question<String> question = new Question<String>(
                rs.getInt("question_id"), 
                rs.getString("question_text"), 
                rs.getString("correct_answer")
        ) {
            @Override
            public boolean checkAnswer(String answer) {
                return this.getCorrectAnswer() != null && this.getCorrectAnswer().equalsIgnoreCase(answer);
            }
        };

        question.setQuizId(rs.getInt("quiz_id"));
        question.setQuestionType(rs.getString("question_type"));
        question.setMarks(rs.getInt("marks"));
        question.setDifficultyLevel(rs.getString("difficulty_level"));
        return question;
    }
}