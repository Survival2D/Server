package com.survival2d.server.controller;

import static com.tvd12.ezyfoxserver.constant.EzyEventNames.USER_REMOVED;

import com.survival2d.server.constant.Commands;
import com.survival2d.server.service.RoomService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractAppEventController;
import com.tvd12.ezyfoxserver.event.EzyUserRemovedEvent;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import com.tvd12.gamebox.entity.MMORoom;
import com.tvd12.gamebox.entity.NormalRoom;
import java.util.List;

@EzySingleton
@EzyEventHandler(USER_REMOVED)
public class UserRemovedController extends EzyAbstractAppEventController<EzyUserRemovedEvent> {

  @EzyAutoBind private RoomService roomService;

  @EzyAutoBind private EzyResponseFactory responseFactory;

  @Override
  public void handle(EzyAppContext ctx, EzyUserRemovedEvent event) {
    logger.info("EzySmashers app: user {} removed", event.getUser());
    String playerName = event.getUser().getName();
    NormalRoom room = roomService.removePlayer(playerName);

    if (!(room instanceof MMORoom)) {
      return;
    }

    List<String> playerNames = roomService.getRoomPlayerNames(room);

    responseFactory
        .newObjectResponse()
        .command(Commands.ANOTHER_EXIT_MMO_ROOM)
        .param("playerName", playerName)
        .usernames(playerNames)
        .execute();
  }
}
