package com.survival2d.server.network.lobby;

import com.survival2d.server.network.match.MatchCommand;
import com.survival2d.server.network.match.request.PlayerMoveRequest;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import lombok.val;

@EzySingleton
public class GetConfigHandler {
  @EzyDoHandle(MatchCommand.GET_CONFIG)
  public void handleGetConfig(EzyUser user) {
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
