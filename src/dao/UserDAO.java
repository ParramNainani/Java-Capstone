package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

/**
 * UserDAO - Data Access Object for User operations
 */
public class UserDAO {

    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, password_hash, email, role FROM users WHERE user_id = ?";
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("email")
                    );
                    user.setUserId(rs.getInt("user_id"));
                    user.setRole(rs.getString("role"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving user by ID: " + e.getMessage());
        }
        return user;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT user_id, username, password_hash, email, role FROM users WHERE username = ?";
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("email")
                    );
                    user.setUserId(rs.getInt("user_id"));
                    user.setRole(rs.getString("role"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving user by username: " + e.getMessage());
        }
        return user;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, email = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}