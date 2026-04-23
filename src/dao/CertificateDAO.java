package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Certificate;

/**
 * CertificateDAO - Data Access Object for Certificate operations.
 */
public class CertificateDAO {

    public boolean insertCertificate(Certificate certificate) {
        String sql = "INSERT INTO certificates (result_id, issue_date, certificate_hash) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setInt(1, certificate.getResultId());
            
            // Map their LocalDate to java.sql.Date
            if (certificate.getIssueDate() != null) {
                stmt.setDate(2, Date.valueOf(certificate.getIssueDate()));
            } else {
                stmt.setNull(2, java.sql.Types.DATE);
            }
            
            stmt.setString(3, certificate.getCertificateHash());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        certificate.setNumericCertificateId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting certificate: " + e.getMessage());
        }
        return false;
    }

    public Certificate getCertificateByResultId(int resultId) {
        String sql = "SELECT * FROM certificates WHERE result_id = ?";
        Certificate certificate = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, resultId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    certificate = mapResultSetToCertificate(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving certificate by result ID: " + e.getMessage());
        }
        return certificate;
    }

    public Certificate validateCertificate(String hash) {
        String sql = "SELECT * FROM certificates WHERE certificate_hash = ?";
        Certificate certificate = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, hash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    certificate = mapResultSetToCertificate(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error validating certificate hash: " + e.getMessage());
        }
        return certificate;
    }

    private Certificate mapResultSetToCertificate(ResultSet rs) throws SQLException {
        Certificate certificate = new Certificate();
        
        // Map backend fields
        certificate.setNumericCertificateId(rs.getInt("certificate_id"));
        certificate.setResultId(rs.getInt("result_id"));
        certificate.setCertificateHash(rs.getString("certificate_hash"));
        
        // Map frontend fields (translating SQL Date back to LocalDate)
        Date sqlDate = rs.getDate("issue_date");
        if (sqlDate != null) {
            certificate.setIssueDate(sqlDate.toLocalDate());
        }
        
        return certificate;
    }
}
