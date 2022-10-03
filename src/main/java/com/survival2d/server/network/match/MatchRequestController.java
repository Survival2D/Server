package com.survival2d.server.network.match;

import com.survival2d.server.network.match.request.PlayerMoveRequest;
import com.survival2d.server.service.MatchingService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@EzyRequestController
@Slf4j
public class MatchRequestController {

  @EzyAutoBind MatchingService matchingService;
  @EzyAutoBind private EzyResponseFactory responseFactory;

  //  @EzyDoHandle(MatchCommand.MATCH_INFO)
  //  public void handleMatchInfo(EzyUser user){
  //    String playerId = user.getName();
  //    val optMatchId = matchingService.getMatchIdOfPlayer(playerId);
  //    if (!optMatchId.isPresent()) {
  //      log.warn("matchId is not present");
  //      return;
  //    }
  //    val matchId = optMatchId.get();
  //    val optMatch = matchingService.getMatchById(matchId);
  //    if (!optMatch.isPresent()) {
  //      log.warn("match is not present");
  //      return;
  //    }
  //    val match = optMatch.get();
  //    responseFactory.newObjectResponse().command(MatchCommand.MATCH_INFO).data(match)..execute();
  //  }

  @EzyDoHandle(MatchCommand.PLAYER_MOVE)
  public void handlePlayerMove(EzyUser user, PlayerMoveRequest request) {
    String playerId = user.getName();
    val optMatchId = matchingService.getMatchIdOfPlayer(playerId);
    if (!optMatchId.isPresent()) {
      log.warn("matchId is not present");
      return;
    }
    val matchId = optMatchId.get();
    val optMatch = matchingService.getMatchById(matchId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    val match = optMatch.get();
    match.onPlayerMove(playerId, request.getDirection(), request.getRotation());
  }
}
