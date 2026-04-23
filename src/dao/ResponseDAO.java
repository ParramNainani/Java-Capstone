package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Response;

/**
 * ResponseDAO - Data Access Object for Response operations.
 */
public class ResponseDAO {

    public boolean insertResponse(Response response) {
        String sql = "INSERT INTO responses (result_id, question_id, selected_option_id, short_answer_text, is_correct) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setInt(1, response.getResultId());
            stmt.setInt(2, response.getQuestionId());
            
            if (response.getSelectedOptionId() != null) {
                stmt.setInt(3, response.getSelectedOptionId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            if (response.getShortAnswerText() != null) {
                stmt.setString(4, response.getShortAnswerText());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            
            stmt.setBoolean(5, response.isCorrect());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        response.setResponseId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting response: " + e.getMessage());
        }
        return false;
    }

    public Response getResponseById(int responseId) {
        String sql = "SELECT * FROM responses WHERE response_id = ?";
        Response response = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, responseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    response = mapResultSetToResponse(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving response by ID: " + e.getMessage());
        }
        return response;
    }

    public List<Response> getResponsesByResultId(int resultId) {
        String sql = "SELECT * FROM responses WHERE result_id = ?";
        List<Response> responses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, resultId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    responses.add(mapResultSetToResponse(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving responses by Result ID: " + e.getMessage());
        }
        return responses;
    }

    private Response mapResultSetToResponse(ResultSet rs) throws SQLException {
        Response response = new Response();
        response.setQuestionId(rs.getInt("question_id"));
        response.setCorrect(rs.getBoolean("is_correct"));
        
        response.setResponseId(rs.getInt("response_id"));
        response.setResultId(rs.getInt("result_id"));
        
        // Properly handle nullable selected_option_id
        int selectedOpt = rs.getInt("selected_option_id");
        if (rs.wasNull()) {
            response.setSelectedOptionId(null);
        } else {
            response.setSelectedOptionId(selectedOpt);
        }
        
        response.setShortAnswerText(rs.getString("short_answer_text"));
        
        return response;
    }
}
