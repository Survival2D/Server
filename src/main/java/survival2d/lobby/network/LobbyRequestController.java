package survival2d.lobby.network;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import lombok.var;
import survival2d.common.ResponseError;
import survival2d.lobby.entity.JoinTeamResult;
import survival2d.lobby.network.request.JoinTeamRequest;
import survival2d.lobby.network.response.CreateTeamResponse;
import survival2d.lobby.network.response.FindMatchResponse;
import survival2d.lobby.network.response.GetConfigResponse;
import survival2d.lobby.network.response.GetUserInfoResponse;
import survival2d.lobby.network.response.JoinTeamResponse;
import survival2d.lobby.network.response.NewUserJoinTeamResponse;
import survival2d.match.config.GameConfig;
import survival2d.service.FindMatchService;
import survival2d.service.LobbyTeamService;
import survival2d.service.MatchingService;
import survival2d.service.entity.LobbyTeam;

@EzyRequestController
@Slf4j
public class LobbyRequestController extends EzyLoggable {

  @EzyAutoBind
  private EzyResponseFactory responseFactory;
  @EzyAutoBind
  private LobbyTeamService lobbyTeamService;
  @EzyAutoBind
  private FindMatchService findMatchService;
  @EzyAutoBind
  private MatchingService matchingService;

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
    var teamId = lobbyTeamService.createTeam();
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
    var username = user.getName();
    var teamId = request.getTeamId();
    var optTeam = lobbyTeamService.getTeam(teamId);
    var responseToRequestedUser =
        responseFactory.newObjectResponse().command(LobbyCommand.JOIN_TEAM).user(user);

    if (!optTeam.isPresent()) {
      responseToRequestedUser
          .data(JoinTeamResponse.builder().result(JoinTeamResult.TEAM_NOT_FOUND).build())
          .execute();
      return;
    }
    var playersAlreadyInTeam = optTeam.get().getPlayers();
    var result = lobbyTeamService.joinTeam(username, teamId);
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
    var username = user.getName();
    var optTeam = lobbyTeamService.getTeamOfPlayer(username);
    if (!optTeam.isPresent()) {
      createTeam(user);
      optTeam = lobbyTeamService.getTeamOfPlayer(username);
    }

    LobbyTeam team = optTeam.get();
    var optMatchId = findMatchService.findMatch(team.getId());
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
    var matchId = optMatchId.get();
    var match = matchingService.getMatchById(matchId).get();
    var allPlayers = match.getAllPlayers();
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

  @EzyDoHandle(LobbyCommand.GET_CONFIG)
  public void handleGetConfig(EzyUser user) {
    var response = GetConfigResponse.builder().map(GameConfig.getInstance()).build();
    responseFactory
        .newObjectResponse()
        .command(LobbyCommand.GET_CONFIG)
        .user(user)
        .data(response)
        .execute();
  }
}
