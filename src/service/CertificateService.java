package service;

import model.Certificate;
import model.Result;
import java.time.LocalDate;
import java.util.UUID;

/**
 * CertificateService - Handles generation of passing certificates.
 */
public class CertificateService {

    public Certificate generateCertificate(Result result, String quizName) {

        if (result == null) return null;

        // Ensure grade exists
        String grade = result.getGrade();

        // Fallback if grade not set
        if (grade == null || grade.isEmpty()) {
            if (result.getPercentage() >= 40) grade = "PASS";
            else grade = "FAIL";
        }

        // Generate certificate only if passed
        if ("PASS".equalsIgnoreCase(grade)) {

            String certId = "CERT-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

            return new Certificate(
                    certId,
                    result.getStudentId(),
                    quizName,
                    LocalDate.now()
            );
        }

        return null;
    }
}