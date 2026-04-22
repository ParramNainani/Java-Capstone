package dao;

import model.QuestionModel;
import java.util.List;

/**
 * DAO interface for Question operations
 */
public interface QuestionDAO {

    void create(QuestionModel question);
    void update(QuestionModel question);
    void delete(int id);
    QuestionModel getById(int id);
    List<QuestionModel> getAll();
}