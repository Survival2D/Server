package com.survival2d.server.network.lobby;

import com.survival2d.server.constant.Commands;
import com.survival2d.server.exception.JoinNotWaitingRoomException;
import com.survival2d.server.network.lobby.response.GetUserInfoResponse;
import com.survival2d.server.request.JoinMMORoomRequest;
import com.survival2d.server.response.AnotherJoinMMOResponse;
import com.survival2d.server.response.CreateMMORoomResponse;
import com.survival2d.server.response.GetMMORoomIdListResponse;
import com.survival2d.server.response.JoinLobbyResponse;
import com.survival2d.server.response.JoinMMORoomResponse;
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
import lombok.val;

@EzyRequestController
public class LobbyRequestController extends EzyLoggable {
  @EzyAutoBind private LobbyService lobbyService;

  @EzyAutoBind private RoomService roomService;

  @EzyAutoBind private EzyResponseFactory responseFactory;

  @EzyDoHandle(LobbyCommand.GET_USER_INFO)
  public void getUserInfo(EzyUser user) {
    responseFactory.newObjectResponse()
        .command(LobbyCommand.GET_USER_INFO)
        .data(GetUserInfoResponse.builder()
            .username(user.getName())
            .build())
        .execute();

  }

  @EzyDoHandle(Commands.JOIN_LOBBY)
  public void joinLobby(EzyUser user) {
    logger.info("user {} join lobby room", user);

    lobbyService.addNewPlayer(user.getName());
    long lobbyRoomId = lobbyService.getRoomId();
    val response = JoinLobbyResponse.builder().lobbyRoomId(lobbyRoomId).build();
    responseFactory.newObjectResponse().command(Commands.JOIN_LOBBY).data(response).user(user)
        .execute();
  }

  @EzyDoHandle(Commands.CREATE_MMO_ROOM)
  public void createMMORoom(EzyUser user) {
    logger.info("user {} create an MMO room", user);
    MMORoom room = roomService.newMMORoom(user);
    val response = CreateMMORoomResponse.builder().roomId(room.getId()).build();
    responseFactory
        .newObjectResponse()
        .command(Commands.CREATE_MMO_ROOM)
        .data(response)
        .user(user)
        .execute();
  }

  @EzyDoHandle(Commands.GET_MMO_ROOM_ID_LIST)
  public void getMMORoomIdList(EzyUser user) {
    logger.info("user {} get MMO room list", user);
    List<Long> mmoRoomIdList = roomService.getMMORoomIdList();
    val response = GetMMORoomIdListResponse.builder().roomIds(mmoRoomIdList).build();

    responseFactory
        .newArrayResponse()
        .command(Commands.GET_MMO_ROOM_ID_LIST)
        .data(response)
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
        .data(JoinMMORoomResponse.builder().roomId(roomId).build())
        .user(user)
        .execute();

    responseFactory
        .newObjectResponse()
        .command(Commands.ANOTHER_JOIN_MMO_ROOM)
        .data(AnotherJoinMMOResponse.builder().playerName(user.getName()).build())
        .usernames(EzyLists.filter(playerNames, it -> !it.equals(user.getName())))
        .execute();
  }
}
