package survival2d.service;

import survival2d.entity.User;
import java.util.List;

public interface UserService {

  void saveUser(User user);

  User createUser(String username, String password);

  User getUser(String username);

  List<User> getAllUsers();
}
