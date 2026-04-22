package main;

import model.MCQQuestion;
import model.Question;
import model.ShortAnswerQuestion;
import model.TrueFalseQuestion;
import service.QuizEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<Question<?>> questions = new ArrayList<>();
        
        Map<String, String> q1Options = new HashMap<>();
        q1Options.put("A", "Java");
        q1Options.put("B", "Python");
        q1Options.put("C", "C++");
        q1Options.put("D", "Ruby");
        questions.add(new MCQQuestion(1, "Which of these is used to build Spring Boot apps?", q1Options, "A"));
        
        questions.add(new TrueFalseQuestion(2, "Java is a purely object-oriented programming language without primitive data types.", false));
        
        questions.add(new ShortAnswerQuestion(3, "Which keyword is used to prevent a class from being inherited?", "final"));

        QuizEngine engine = new QuizEngine();
        engine.loadQuestions(questions);
        
        System.out.println("Starting Quiz Engine!");
        System.out.println("You have 20 seconds to complete the quiz.");
        System.out.println("-------------------------------------------");
        
        engine.startQuiz(20);
        
        engine.evaluate();
    }
}
