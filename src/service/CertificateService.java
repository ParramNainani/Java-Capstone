package service;

import model.Certificate;
import model.Result;
import util.Constants;
import java.time.LocalDate;
import java.util.UUID;

/**
 * CertificateService - Handles generation of passing certificates.
 */
public class CertificateService {

    public Certificate generateCertificate(Result result, String quizName) {
        if (result != null && "PASS".equalsIgnoreCase(result.getGrade())) {
            String certId = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return new Certificate(certId, result.getStudentId(), quizName, LocalDate.now());
        }
        return null; 
    }
}
