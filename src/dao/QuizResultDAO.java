package dao;

import model.Result;
import java.util.List;

/**
 * DAO interface for Result operations
 */
public interface QuizResultDAO {

    void create(Result result);
    void update(Result result);
    void delete(int id);
    Result getById(int id);
    List<Result> getAll();
}