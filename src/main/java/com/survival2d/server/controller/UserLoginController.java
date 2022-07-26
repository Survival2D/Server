package com.survival2d.server.controller;

import static com.tvd12.ezyfoxserver.constant.EzyEventNames.USER_LOGIN;

import com.survival2d.server.config.LoginConfig;
import com.survival2d.server.entity.User;
import com.survival2d.server.service.UserService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.sercurity.EzySHA256;
import com.tvd12.ezyfoxserver.constant.EzyLoginError;
import com.tvd12.ezyfoxserver.context.EzyPluginContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractPluginEventController;
import com.tvd12.ezyfoxserver.event.EzyUserLoginEvent;
import com.tvd12.ezyfoxserver.exception.EzyLoginErrorException;

@EzySingleton
@EzyEventHandler(USER_LOGIN)
public class UserLoginController extends EzyAbstractPluginEventController<EzyUserLoginEvent> {

  @EzyAutoBind private UserService userService;

  @Override
  public void handle(EzyPluginContext ctx, EzyUserLoginEvent event) {
    logger.info("{} login in", event.getUsername());

    String username = event.getUsername();
    if (EzyStrings.isNoContent(username)) {
      throw new EzyLoginErrorException(EzyLoginError.INVALID_USERNAME);
    }

    String password = event.getPassword();
    if (LoginConfig.isEnableAuth && EzyStrings.isNoContent(password)) {
      throw new EzyLoginErrorException(EzyLoginError.INVALID_PASSWORD);
    }

    String encodedPassword = encodePassword(password);
    User user = userService.getUser(username);
    if (user == null) {
      logger.info("User doesn't exist in db, create a new one!");
      user = userService.createUser(username, encodedPassword);
      userService.saveUser(user);
      return;
    }

    if (LoginConfig.isEnableAuth) {
      if (!user.getPassword().equals(encodedPassword)) {
        throw new EzyLoginErrorException(EzyLoginError.INVALID_PASSWORD);
      }
      logger.info("user and password match, accept user: {}", username);
      return;
    }

    logger.info("Server not enable authentication, login directly");
  }

  private String encodePassword(String password) {
    return EzySHA256.cryptUtfToLowercase(password);
  }
}
