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
        String sql = "INSERT INTO questions (quiz_id, question_text, correct_answer, question_type, marks, difficulty_level, option_a, option_b, option_c, option_d) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getQuestionText());
            stmt.setString(3, question.getCorrectAnswer() != null ? question.getCorrectAnswer().toString() : null);
            stmt.setString(4, question.getQuestionType());
            stmt.setInt(5, question.getMarks());
            stmt.setString(6, question.getDifficultyLevel());
            
            if (question instanceof model.MCQQuestion) {
                model.MCQQuestion mcq = (model.MCQQuestion) question;
                java.util.Map<String, String> opts = mcq.getOptions();
                stmt.setString(7, opts.get("A"));
                stmt.setString(8, opts.get("B"));
                stmt.setString(9, opts.get("C"));
                stmt.setString(10, opts.get("D"));
            } else {
                stmt.setString(7, null);
                stmt.setString(8, null);
                stmt.setString(9, null);
                stmt.setString(10, null);
            }
            
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
        String sql = "UPDATE questions SET quiz_id = ?, question_text = ?, correct_answer = ?, question_type = ?, marks = ?, difficulty_level = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ? WHERE question_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getQuestionText());
            stmt.setString(3, question.getCorrectAnswer() != null ? question.getCorrectAnswer().toString() : null);
            stmt.setString(4, question.getQuestionType());
            stmt.setInt(5, question.getMarks());
            stmt.setString(6, question.getDifficultyLevel());
            
            if (question instanceof model.MCQQuestion) {
                model.MCQQuestion mcq = (model.MCQQuestion) question;
                java.util.Map<String, String> opts = mcq.getOptions();
                stmt.setString(7, opts.get("A"));
                stmt.setString(8, opts.get("B"));
                stmt.setString(9, opts.get("C"));
                stmt.setString(10, opts.get("D"));
            } else {
                stmt.setString(7, null);
                stmt.setString(8, null);
                stmt.setString(9, null);
                stmt.setString(10, null);
            }
            
            stmt.setInt(11, question.getQuestionId());
            
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

    public boolean deleteQuestionsByQuizId(int quizId) {
        String sql = "DELETE FROM questions WHERE quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, quizId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting questions by quiz ID: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper to map result set into the abstract Question form. 
     */
    private Question<String> mapResultSetToQuestion(ResultSet rs) throws SQLException {
        String optA = rs.getString("option_a");
        String optB = rs.getString("option_b");
        String optC = rs.getString("option_c");
        String optD = rs.getString("option_d");
        
        Question<String> question;
        if (optA != null || optB != null) {
            java.util.Map<String, String> opts = new java.util.HashMap<>();
            opts.put("A", optA);
            opts.put("B", optB);
            opts.put("C", optC);
            opts.put("D", optD);
            question = new model.MCQQuestion(
                    rs.getInt("question_id"), 
                    rs.getString("question_text"), 
                    opts,
                    rs.getString("correct_answer")
            );
        } else {
            question = new Question<String>(
                    rs.getInt("question_id"), 
                    rs.getString("question_text"), 
                    rs.getString("correct_answer")
            ) {
                @Override
                public boolean checkAnswer(String answer) {
                    return this.getCorrectAnswer() != null && this.getCorrectAnswer().equalsIgnoreCase(answer);
                }
            };
        }

        question.setQuizId(rs.getInt("quiz_id"));
        question.setQuestionType(rs.getString("question_type"));
        question.setMarks(rs.getInt("marks"));
        question.setDifficultyLevel(rs.getString("difficulty_level"));
        return question;
    }

    public int getTotalQuestionsCount() {
        String sql = "SELECT COUNT(*) FROM questions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total questions count: " + e.getMessage());
        }
        return 0;
    }
}