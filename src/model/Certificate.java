package model;

import java.time.LocalDate;

/**
 * Certificate - Represents a generated certificate for passing a quiz.
 */
public class Certificate {
    private String certificateId;
    private String studentId;
    private String quizName;
    private LocalDate issueDate;
    private int numericCertificateId;
    private int resultId;
    private String certificateHash;

    public Certificate() {}

    public Certificate(String certificateId, String studentId, String quizName, LocalDate issueDate) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.quizName = quizName;
        this.issueDate = issueDate;
    }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getQuizName() { return quizName; }
    public void setQuizName(String quizName) { this.quizName = quizName; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public int getNumericCertificateId() { return numericCertificateId; }
    public void setNumericCertificateId(int numericCertificateId) { this.numericCertificateId = numericCertificateId; }

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public String getCertificateHash() { return certificateHash; }
    public void setCertificateHash(String certificateHash) { this.certificateHash = certificateHash; }
}
