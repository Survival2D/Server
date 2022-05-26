package com.survival2d.plugin.controller;

import static com.tvd12.ezyfoxserver.constant.EzyEventNames.USER_LOGIN;

import com.survival2d.common.entity.ChatUser;
import com.survival2d.common.service.ChatMaxIdService;
import com.survival2d.common.service.ChatUserService;
import com.survival2d.plugin.service.WelcomeService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfoxserver.context.EzyPluginContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractPluginEventController;
import com.tvd12.ezyfoxserver.event.EzyUserLoginEvent;

@EzySingleton
@EzyEventHandler(USER_LOGIN)
public class UserLoginController extends EzyAbstractPluginEventController<EzyUserLoginEvent> {

  @EzyAutoBind private WelcomeService welcomeService;



  private ChatUserService userService;

  @Override
  public void handle(EzyPluginContext ctx, EzyUserLoginEvent event) {
    logger.info("{} login in", welcomeService.welcome(event.getUsername()));

    String username = event.getUsername();
    String password = event.getPassword();

    ChatUser user = userService.getUser(username);

    if (user == null) {
      logger.info("User không tồn tại trong db, tạo mới!");
      user = userService.createUser(username, password);
      userService.saveUser(user);
    } else {
      logger.info("User đã tồn tại");
    }
  }
}
