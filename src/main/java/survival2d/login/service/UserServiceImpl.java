package survival2d.login.service;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import java.util.List;
import lombok.Setter;
import survival2d.login.entity.User;
import survival2d.login.repo.UserRepo;
import survival2d.service.MaxIdService;

@Setter
@EzySingleton("userService")
public class UserServiceImpl implements UserService {

  @EzyAutoBind
  private UserRepo userRepo;

  @EzyAutoBind
  private MaxIdService maxIdService;

  @Override
  public void saveUser(User user) {
    userRepo.save(user);
  }

  @Override
  public User createUser(String username, String password) {
    User user = new User();
    user.setId(maxIdService.incrementAndGet("user"));
    user.setUsername(username);
    user.setPassword(password);
    userRepo.save(user);
    return user;
  }

  @Override
  public User getUser(String username) {
    return userRepo.findByField("username", username);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepo.findAll();
  }
}
