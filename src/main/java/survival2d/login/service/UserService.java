package survival2d.login.service;

import java.util.List;
import survival2d.login.entity.User;

public interface UserService {

  void saveUser(User user);

  User createUser(String username, String password);

  User getUser(String username);

  List<User> getAllUsers();
}
