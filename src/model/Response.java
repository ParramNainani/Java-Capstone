package model;

/**
 * Response - Represents a student's answer to a specific question.
 */
public class Response {
    private String studentId;
    private int questionId;
    private String chosenAnswer;
    private boolean isCorrect;
    private int responseId;
    private int resultId;
    private Integer selectedOptionId;
    private String shortAnswerText;

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
    public int getResponseId() { return responseId; }
    public void setResponseId(int responseId) { this.responseId = responseId; }

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public Integer getSelectedOptionId() { return selectedOptionId; }
    public void setSelectedOptionId(Integer selectedOptionId) { this.selectedOptionId = selectedOptionId; }

    public String getShortAnswerText() { return shortAnswerText; }
    public void setShortAnswerText(String shortAnswerText) { this.shortAnswerText = shortAnswerText; }
}
