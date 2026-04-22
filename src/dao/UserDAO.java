package dao;

import model.User;
import java.util.List;

/**
 * DAO interface for User operations
 */
public interface UserDAO {

    void create(User user);
    void update(User user);
    void delete(int id);
    User getById(int id);
    List<User> getAll();
}