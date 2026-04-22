package service;

import model.MCQQuestion;
import model.Question;
import model.ShortAnswerQuestion;
import model.TrueFalseQuestion;
import util.TimerThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class QuizEngine {
    private List<Question<?>> questions;
    private Map<Integer, Object> answers;
    private TimerThread timerThread;

    public QuizEngine() {
        this.answers = new HashMap<>();
    }

    public void loadQuestions(List<Question<?>> questions) {
        this.questions = questions;
    }

    public void submitAnswer(int questionId, Object answer) {
        answers.put(questionId, answer);
    }

    public void startQuiz(int timeLimit) {
        if (questions == null || questions.isEmpty()) {
            System.out.println("No questions loaded.");
            return;
        }

        timerThread = new TimerThread(timeLimit);
        timerThread.start();
        
        Scanner scanner = new Scanner(System.in);
        
        for (Question<?> q : questions) {
            if (timerThread.isTimeUp()) {
                break;
            }
            
            System.out.println("\nQuestion " + q.getQuestionId() + ": " + q.getQuestionText());
            
            if (q instanceof MCQQuestion) {
                MCQQuestion mcq = (MCQQuestion) q;
                for (Map.Entry<String, String> option : mcq.getOptions().entrySet()) {
                    System.out.println(option.getKey() + ") " + option.getValue());
                }
                System.out.print("Your answer (e.g. A, B, C, D): ");
                
                String ans = waitForInput(scanner);
                if (ans != null) {
                    submitAnswer(q.getQuestionId(), ans.trim());
                } else {
                    break;
                }
            } else if (q instanceof TrueFalseQuestion) {
                System.out.print("True / False: ");
                String ansStr = waitForInput(scanner);
                if (ansStr != null) {
                    boolean ans = Boolean.parseBoolean(ansStr.trim());
                    submitAnswer(q.getQuestionId(), ans);
                } else {
                    break;
                }
            } else if (q instanceof ShortAnswerQuestion) {
                System.out.print("Your answer: ");
                String ans = waitForInput(scanner);
                if (ans != null) {
                    submitAnswer(q.getQuestionId(), ans.trim());
                } else {
                    break;
                }
            }
        }
        
        if (!timerThread.isTimeUp()) {
            timerThread.interrupt(); // stop the timer since quiz is done
            System.out.println("\nQuiz completed within the time limit.");
        }
    }

    // A helper to allow early breaking if the timer completes while waiting for answer.
    // In strict console IO, hasNextLine can block. We are using standard Scanner which might 
    // force the user to press Enter at least one more time if they were typing when time ended.
    private String waitForInput(Scanner scanner) {
        try {
            while (!timerThread.isTimeUp()) {
                if (System.in.available() > 0) {
                    return scanner.nextLine();
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            // Ignored
        }
        // Fallback for cases where available() might not work perfectly with Scanner or if time is up
        if (!timerThread.isTimeUp() && scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null; // Time is up or no input
    }

    public void evaluate() {
        System.out.println("\nEvaluating answers...");
        int score = 0;
        for (Question<?> question : questions) {
            Object userAnswer = answers.get(question.getQuestionId());
            if (userAnswer != null && checkGenericAnswer(question, userAnswer)) {
                score++;
            }
        }
        System.out.println("=== Quiz Results ===");
        System.out.println("Total Score: " + score + " / " + questions.size());
    }

    @SuppressWarnings("unchecked")
    private <T> boolean checkGenericAnswer(Question<T> question, Object answer) {
        try {
            return question.checkAnswer((T) answer);
        } catch (ClassCastException e) {
            return false;
        }
    }
}
