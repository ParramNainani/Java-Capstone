package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private static final String QUESTIONS_FILE = System.getProperty("user.home") + File.separator + ".examify_questions.dat";
    private static final String QUIZZES_FILE = System.getProperty("user.home") + File.separator + ".examify_quizzes.dat";

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadItems(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Could not load from " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static <T> void saveItems(String filePath, List<T> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(items);
        } catch (IOException e) {
            System.err.println("Could not save to " + filePath + ": " + e.getMessage());
        }
    }

    public static String getQuestionsFilePath() {
        return QUESTIONS_FILE;
    }

    public static String getQuizzesFilePath() {
        return QUIZZES_FILE;
    }
}
