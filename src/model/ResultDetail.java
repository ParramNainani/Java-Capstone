package model;

public class ResultDetail {
    private int resultId;
    private String studentName;
    private String quizTitle;
    private int score;
    private int totalMarks;
    
    public ResultDetail(int resultId, String studentName, String quizTitle, int score, int totalMarks) {
        this.resultId = resultId;
        this.studentName = studentName;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalMarks = totalMarks;
    }

    public int getResultId() { return resultId; }
    public String getStudentName() { return studentName; }
    public String getQuizTitle() { return quizTitle; }
    public int getScore() { return score; }
    public int getTotalMarks() { return totalMarks; }

    public int getPercentage() {
        if (totalMarks == 0) return 0;
        return (int) Math.round((score * 100.0) / totalMarks);
    }
}
