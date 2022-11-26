package survival2d.network.plugin;

import static com.tvd12.ezyfoxserver.constant.EzyEventNames.USER_LOGIN;

import survival2d.config.LoginConfig;
import survival2d.entity.User;
import survival2d.service.UserService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.security.EzySHA256;
import com.tvd12.ezyfoxserver.constant.EzyLoginError;
import com.tvd12.ezyfoxserver.context.EzyPluginContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractPluginEventController;
import com.tvd12.ezyfoxserver.event.EzyUserLoginEvent;
import com.tvd12.ezyfoxserver.exception.EzyLoginErrorException;
import org.apache.commons.lang3.RandomStringUtils;

@EzySingleton
@EzyEventHandler(USER_LOGIN)
public class UserLoginController extends EzyAbstractPluginEventController<EzyUserLoginEvent> {

  public static final int DEFAULT_RANDOM_USERNAME_LENGTH = 10;

  @EzyAutoBind
  private UserService userService;

  @Override
  public void handle(EzyPluginContext ctx, EzyUserLoginEvent event) {

    String username = event.getUsername();
    logger.info("{} login in", username);

    if (LoginConfig.isEnableAuth) {
      String password = event.getPassword();
      loginWithAuth(username, password);
    } else {
      if (EzyStrings.isNoContent(username)) {
        username = RandomStringUtils.randomAlphabetic(DEFAULT_RANDOM_USERNAME_LENGTH);
      }
      event.setUsername(username);
      loginWithoutAuth(username);
    }
  }

  private String encodePassword(String password) {
    return EzySHA256.cryptUtfToLowercase(password);
  }

  private void loginWithAuth(String username, String password) {
    if (EzyStrings.isNoContent(username)) {
      throw new EzyLoginErrorException(EzyLoginError.INVALID_USERNAME);
    }
    if (LoginConfig.isEnableAuth && EzyStrings.isNoContent(password)) {
      throw new EzyLoginErrorException(EzyLoginError.INVALID_PASSWORD);
    }
    User user = userService.getUser(username);
    String encodedPassword = encodePassword(password);
    if (user == null) {
      createUser(username, encodedPassword);
      return;
    }
    if (!user.getPassword().equals(encodedPassword)) {
      logger.info("User {} login with wrong password", username);
      throw new EzyLoginErrorException(EzyLoginError.INVALID_PASSWORD);
    }
    logger.info("User {} login with right password", username);
  }

  private void loginWithoutAuth(String username) {
    User user = userService.getUser(username);
    if (user == null) {
      createUser(username, encodePassword(""));
      return;
    }
    logger.info("Server not enable authentication, user {} login directly", username);
  }

  private void createUser(String username, String encodedPassword) {
    logger.info("User {} doesn't exist in db, create a new one!", username);
    User user = userService.createUser(username, encodedPassword);
    userService.saveUser(user);
  }
}
