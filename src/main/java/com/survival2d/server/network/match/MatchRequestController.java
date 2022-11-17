package com.survival2d.server.network.match;

import com.survival2d.server.game.action.PlayerAttack;
import com.survival2d.server.game.action.PlayerChangeWeapon;
import com.survival2d.server.game.action.PlayerDropItem;
import com.survival2d.server.game.action.PlayerMove;
import com.survival2d.server.game.action.PlayerReloadWeapon;
import com.survival2d.server.game.action.PlayerTakeItem;
import com.survival2d.server.network.match.request.PlayerAttackRequest;
import com.survival2d.server.network.match.request.PlayerChangeWeaponRequest;
import com.survival2d.server.network.match.request.PlayerDropItemRequest;
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

  @EzyAutoBind
  MatchingService matchingService;
  @EzyAutoBind
  private EzyResponseFactory responseFactory;

  @EzyDoHandle(MatchCommand.MATCH_INFO)
  public void handleMatchInfo(EzyUser user) {
    val playerId = user.getName();
    val optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    val match = optMatch.get();
    responseFactory.newObjectResponse().command(MatchCommand.MATCH_INFO).data(match)
        .username(playerId).execute();
  }

  @EzyDoHandle(MatchCommand.PLAYER_MOVE)
  public void handlePlayerMove(EzyUser user, PlayerMoveRequest request) {
    val playerId = user.getName();
    val optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    val match = optMatch.get();
    match.onReceivePlayerAction(
        playerId, new PlayerMove(request.getDirection(), request.getRotation()));
  }

  @EzyDoHandle(MatchCommand.PLAYER_CHANGE_WEAPON)
  public void handlePlayerChangeWeapon(EzyUser user, PlayerChangeWeaponRequest request) {
    val playerId = user.getName();
    val optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    val match = optMatch.get();
    match.onReceivePlayerAction(playerId, new PlayerChangeWeapon(request.getSlot()));
  }

  @EzyDoHandle(MatchCommand.PLAYER_ATTACK)
  public void handlePlayerAttack(EzyUser user, PlayerAttackRequest request) {
    val playerId = user.getName();
    val optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    val match = optMatch.get();
    match.onReceivePlayerAction(playerId, new PlayerAttack());
  }

  @EzyDoHandle(MatchCommand.PLAYER_RELOAD)
  public void handlePlayerReload(EzyUser user) {
    val playerId = user.getName();
    val optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    val match = optMatch.get();
    match.onReceivePlayerAction(playerId, new PlayerReloadWeapon());
  }

  @EzyDoHandle(MatchCommand.TAKE_ITEM)
  public void handleTakeItem(EzyUser user) {
    val playerId = user.getName();
    val optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    val match = optMatch.get();
    match.onReceivePlayerAction(playerId, new PlayerTakeItem());
  }

  @EzyDoHandle(MatchCommand.DROP_ITEM)
  public void handleDropItem(EzyUser user, PlayerDropItemRequest request) {
    val playerId = user.getName();
    val optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    val match = optMatch.get();
    match.onReceivePlayerAction(playerId, new PlayerDropItem(request.getItemId()));
  }
}
