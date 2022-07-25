package com.survival2d.server.controller;

import static com.tvd12.ezyfoxserver.constant.EzyEventNames.USER_LOGIN;

import com.survival2d.server.entity.GamePlayerId;
import com.survival2d.server.entity.User;
import com.survival2d.server.repo.CredentialRepo;
import com.survival2d.server.service.UserService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.sercurity.EzySHA256;
import com.tvd12.ezyfoxserver.constant.EzyEventNames;
import com.tvd12.ezyfoxserver.constant.EzyLoginError;
import com.tvd12.ezyfoxserver.context.EzyPluginContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractPluginEventController;
import com.tvd12.ezyfoxserver.event.EzyUserLoginEvent;
import com.tvd12.ezyfoxserver.exception.EzyLoginErrorException;
import lombok.Setter;

@EzySingleton
@EzyEventHandler(USER_LOGIN)
public class UserLoginController extends EzyAbstractPluginEventController<EzyUserLoginEvent> {

  @EzyAutoBind private UserService userService;

  @Override
  public void handle(EzyPluginContext ctx, EzyUserLoginEvent event) {
    logger.info("{} login in", event.getUsername());

    String username = event.getUsername();
    String password = encodePassword(event.getPassword());

    User user = userService.getUser(username);

    if (user == null) {
      logger.info("User doesn't exist in db, create a new one!");
      user = userService.createUser(username, password);
      userService.saveUser(user);
    }

    if (!user.getPassword().equals(password)) {
      throw new EzyLoginErrorException(EzyLoginError.INVALID_PASSWORD);
    }

    logger.info("user and password match, accept user: {}", username);
  }

  private String encodePassword(String password) {
    return EzySHA256.cryptUtfToLowercase(password);
  }
}
//@Setter
//@EzySingleton
//@EzyEventHandler(EzyEventNames.USER_LOGIN)
//public class UserLoginController
//    extends EzyAbstractPluginEventController<EzyUserLoginEvent> {
//
//  @EzyAutoBind
//  private CredentialRepo credentialRepo;
//
//  @Override
//  public void handle(EzyPluginContext ctx, EzyUserLoginEvent event) {
//    logger.info("Space Game - user {} login in", event.getUsername());
//    String player = event.getUsername();
//    if(EzyStrings.isNoContent(player))
//      throw new EzyLoginErrorException(EzyLoginError.INVALID_USERNAME);
//    String password = event.getPassword();
//    if(EzyStrings.isNoContent(password))
//      throw new EzyLoginErrorException(EzyLoginError.INVALID_PASSWORD);
//    EzyArray data = event.getData();
//    String game = data.get(0);
//    GamePlayerId gamePlayerId = new GamePlayerId(game, player);
//    Credential credential = credentialRepo.findById(gamePlayerId);
//    String sha256Password = EzySHA256.cryptUtfToLowercase(password);
//    if(credential != null) {
//      if(!credential.getPassword().equals(sha256Password))
//        throw new EzyLoginErrorException(EzyLoginError.INVALID_PASSWORD);
//    }
//    else {
//      credentialRepo.save(new Credential(gamePlayerId, sha256Password));
//    }
//  }
//}
