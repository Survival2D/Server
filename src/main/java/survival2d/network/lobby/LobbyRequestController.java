package survival2d.network.lobby;

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
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import survival2d.constant.Commands;
import survival2d.exception.JoinNotWaitingRoomException;
import survival2d.network.lobby.entity.JoinTeamResult;
import survival2d.network.lobby.entity.ResponseError;
import survival2d.network.lobby.request.JoinTeamRequest;
import survival2d.network.lobby.response.CreateTeamResponse;
import survival2d.network.lobby.response.FindMatchResponse;
import survival2d.network.lobby.response.GetUserInfoResponse;
import survival2d.network.lobby.response.JoinTeamResponse;
import survival2d.network.lobby.response.NewUserJoinTeamResponse;
import survival2d.request.JoinMMORoomRequest;
import survival2d.response.AnotherJoinMMOResponse;
import survival2d.response.CreateMMORoomResponse;
import survival2d.response.GetMMORoomIdListResponse;
import survival2d.response.JoinLobbyResponse;
import survival2d.response.JoinMMORoomResponse;
import survival2d.service.FindMatchService;
import survival2d.service.LobbyService;
import survival2d.service.LobbyTeamService;
import survival2d.service.MatchingService;
import survival2d.service.RoomService;
import survival2d.service.domain.LobbyTeam;

@EzyRequestController
@Slf4j
public class LobbyRequestController extends EzyLoggable {

  @EzyAutoBind private LobbyService lobbyService;

  @EzyAutoBind private RoomService roomService;

  @EzyAutoBind private EzyResponseFactory responseFactory;
  @EzyAutoBind private LobbyTeamService lobbyTeamService;
  @EzyAutoBind private FindMatchService findMatchService;
  @EzyAutoBind private MatchingService matchingService;

  @EzyDoHandle(LobbyCommand.GET_USER_INFO)
  public void getUserInfo(EzyUser user) {
    responseFactory
        .newObjectResponse()
        .command(LobbyCommand.GET_USER_INFO)
        .data(GetUserInfoResponse.builder().username(user.getName()).build())
        .user(user)
        .execute();
  }

  @EzyDoHandle(LobbyCommand.CREATE_TEAM)
  public void createTeam(EzyUser user) {
    val teamId = lobbyTeamService.createTeam();
    lobbyTeamService.joinTeam(user.getName(), teamId);
    responseFactory
        .newObjectResponse()
        .command(LobbyCommand.CREATE_TEAM)
        .data(CreateTeamResponse.builder().teamId(teamId).build())
        .user(user)
        .execute();
  }

  @EzyDoHandle(LobbyCommand.JOIN_TEAM)
  public void joinTeam(EzyUser user, JoinTeamRequest request) {
    val username = user.getName();
    val teamId = request.getTeamId();
    val optTeam = lobbyTeamService.getTeam(teamId);
    val responseToRequestedUser =
        responseFactory.newObjectResponse().command(LobbyCommand.JOIN_TEAM).user(user);

    if (!optTeam.isPresent()) {
      responseToRequestedUser
          .data(JoinTeamResponse.builder().result(JoinTeamResult.TEAM_NOT_FOUND).build())
          .execute();
      return;
    }
    val playersAlreadyInTeam = optTeam.get().getPlayers();
    val result = lobbyTeamService.joinTeam(username, teamId);
    if (!result) {
      responseToRequestedUser
          .data(JoinTeamResponse.builder().result(JoinTeamResult.ERROR_WHEN_JOIN).build())
          .execute();
      return;
    }
    responseToRequestedUser
        .data(JoinTeamResponse.builder().result(JoinTeamResult.SUCCESS).teamId(teamId).build())
        .execute();
    // Response to other player in team
    responseFactory
        .newObjectResponse()
        .command(LobbyCommand.NEW_USER_JOIN_TEAM)
        .data(NewUserJoinTeamResponse.builder().username(username).build())
        .usernames(playersAlreadyInTeam)
        .execute();
  }

  @EzyDoHandle(LobbyCommand.FIND_MATCH)
  public void findMatch(EzyUser user) {
    val username = user.getName();
    var optTeam = lobbyTeamService.getTeamOfPlayer(username);
    if (!optTeam.isPresent()) {
      createTeam(user);
      optTeam = lobbyTeamService.getTeamOfPlayer(username);
    }

    LobbyTeam team = optTeam.get();
    val optMatchId = findMatchService.findMatch(team.getId());
    if (!optMatchId.isPresent()) {
      log.info("Not found match yet!");
      //      responseFactory
      //          .newObjectResponse()
      //          .command(LobbyCommand.FIND_MATCH)
      //          .data(FindMatchResponse.builder().result(FindMatchResult.NOT_FOUND_YET).build())
      //          .user(user)
      //          .execute();
      return;
    }
    val matchId = optMatchId.get();
    val match = matchingService.getMatchById(matchId).get();
    val allPlayers = match.getAllPlayers();
    responseFactory
        .newObjectResponse()
        .command(LobbyCommand.FIND_MATCH)
        .data(FindMatchResponse.builder().result(ResponseError.SUCCESS).matchId(matchId).build())
        .usernames(allPlayers)
        .execute();
    match.responseMatchInfo();
//    responseFactory
//        .newObjectResponse()
//        .command(MatchCommand.MATCH_INFO)
//        .data(match)
//        .usernames(allPlayers)
//        .execute();
  }

  @EzyDoHandle(Commands.JOIN_LOBBY)
  public void joinLobby(EzyUser user) {
    logger.info("user {} join lobby room", user);

    lobbyService.addNewPlayer(user.getName());
    long lobbyRoomId = lobbyService.getRoomId();
    val response = JoinLobbyResponse.builder().lobbyRoomId(lobbyRoomId).build();
    responseFactory
        .newObjectResponse()
        .command(Commands.JOIN_LOBBY)
        .data(response)
        .user(user)
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

  @EzyDoHandle(LobbyCommand.PING)
  public void ping(EzyUser user) {
    responseFactory.newObjectResponse().command(LobbyCommand.PING).user(user).execute();
  }
}
