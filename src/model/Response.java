package model;

/**
 * Response - Represents a student's answer to a specific question.
 */
public class Response {
    private String studentId;
    private int questionId;
    private String chosenAnswer;
    private boolean isCorrect;

    public Response() {}

    public Response(String studentId, int questionId, String chosenAnswer, boolean isCorrect) {
        this.studentId = studentId;
        this.questionId = questionId;
        this.chosenAnswer = chosenAnswer;
        this.isCorrect = isCorrect;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getChosenAnswer() { return chosenAnswer; }
    public void setChosenAnswer(String chosenAnswer) { this.chosenAnswer = chosenAnswer; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean isCorrect) { this.isCorrect = isCorrect; }
}
