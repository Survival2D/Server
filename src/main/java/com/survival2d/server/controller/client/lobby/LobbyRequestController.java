package com.survival2d.server.controller.client.lobby;

import com.survival2d.server.constant.Commands;
import com.survival2d.server.exception.JoinNotWaitingRoomException;
import com.survival2d.server.request.JoinMMORoomRequest;
import com.survival2d.server.service.LobbyService;
import com.survival2d.server.service.RoomService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfox.io.EzyLists;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import com.tvd12.gamebox.constant.RoomStatus;
import com.tvd12.gamebox.entity.MMORoom;
import java.util.List;

@EzyRequestController
public class LobbyRequestController extends EzyLoggable {
  @EzyAutoBind private LobbyService lobbyService;

  @EzyAutoBind private RoomService roomService;

  @EzyAutoBind private EzyResponseFactory responseFactory;

  @EzyDoHandle(Commands.JOIN_LOBBY)
  public void joinLobby(EzyUser user) {
    logger.info("user {} join lobby room", user);

    lobbyService.addNewPlayer(user.getName());
    long lobbyRoomId = lobbyService.getRoomId();

    responseFactory
        .newObjectResponse()
        .command(Commands.JOIN_LOBBY)
        .param("lobbyRoomId", lobbyRoomId)
        .user(user)
        .execute();
  }

  @EzyDoHandle(Commands.CREATE_MMO_ROOM)
  public void createMMORoom(EzyUser user) {
    logger.info("user {} create an MMO room", user);
    MMORoom room = roomService.newMMORoom(user);

    responseFactory
        .newObjectResponse()
        .command(Commands.CREATE_MMO_ROOM)
        .param("roomId", room.getId())
        .user(user)
        .execute();
  }

  @EzyDoHandle(Commands.GET_MMO_ROOM_ID_LIST)
  public void getMMORoomIdList(EzyUser user) {
    logger.info("user {} get MMO room list", user);
    List<Long> mmoRoomIdList = roomService.getMMORoomIdList();
    responseFactory
        .newArrayResponse()
        .command(Commands.GET_MMO_ROOM_ID_LIST)
        .param(mmoRoomIdList)
        .user(user)
        .execute();
  }

  @EzyDoHandle(Commands.JOIN_MMO_ROOM)
  public void joinMMORoom(EzyUser user, JoinMMORoomRequest request) {
    logger.info("user {} join room {}", user.getName(), request.getRoomId());
    long roomId = request.getRoomId();
    MMORoom room = roomService.playerJoinMMORoom(user.getName(), roomId);
    if (room.getStatus() != RoomStatus.WAITING) {
      throw new JoinNotWaitingRoomException(user.getName(), room);
    }
    List<String> playerNames = roomService.getRoomPlayerNames(room);

    responseFactory
        .newObjectResponse()
        .command(Commands.JOIN_MMO_ROOM)
        .param("roomId", roomId)
        .user(user)
        .execute();

    responseFactory
        .newObjectResponse()
        .command(Commands.ANOTHER_JOIN_MMO_ROOM)
        .param("playerName", user.getName())
        .usernames(EzyLists.filter(playerNames, it -> !it.equals(user.getName())))
        .execute();
  }
}
