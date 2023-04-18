package survival2d.match.network;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import lombok.extern.slf4j.Slf4j;
import survival2d.match.action.ActionAttack;
import survival2d.match.action.ActionChangeWeapon;
import survival2d.match.action.ActionDropItem;
import survival2d.match.action.ActionMove;
import survival2d.match.action.ActionReloadWeapon;
import survival2d.match.action.ActionTakeItem;
import survival2d.match.network.request.PlayerAttackRequest;
import survival2d.match.network.request.PlayerChangeWeaponRequest;
import survival2d.match.network.request.PlayerDropItemRequest;
import survival2d.match.network.request.PlayerMoveRequest;

@EzyRequestController
@Slf4j
public class MatchRequestController {

  @EzyAutoBind
  MatchingService matchingService;
  @EzyAutoBind
  private EzyResponseFactory responseFactory;

  @EzyDoHandle(MatchCommand.MATCH_INFO)
  public void handleMatchInfo(EzyUser user) {
    var playerId = user.getName();
    var optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    var match = optMatch.get();
    responseFactory.newObjectResponse().command(MatchCommand.MATCH_INFO).data(match)
        .username(playerId).execute();
  }

  @EzyDoHandle(MatchCommand.PLAYER_MOVE)
  public void handlePlayerMove(EzyUser user, PlayerMoveRequest request) {
    var playerId = user.getName();
    var optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    var match = optMatch.get();
    match.onReceivePlayerAction(
        playerId, new ActionMove(request.getDirection(), request.getRotation()));
  }

  @EzyDoHandle(MatchCommand.PLAYER_CHANGE_WEAPON)
  public void handlePlayerChangeWeapon(EzyUser user, PlayerChangeWeaponRequest request) {
    var playerId = user.getName();
    var optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    var match = optMatch.get();
    match.onReceivePlayerAction(playerId, new ActionChangeWeapon(request.getSlot()));
  }

  @EzyDoHandle(MatchCommand.PLAYER_ATTACK)
  public void handlePlayerAttack(EzyUser user, PlayerAttackRequest request) {
    var playerId = user.getName();
    var optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    var match = optMatch.get();
    match.onReceivePlayerAction(playerId, new ActionAttack());
  }

  @EzyDoHandle(MatchCommand.PLAYER_RELOAD)
  public void handlePlayerReload(EzyUser user) {
    var playerId = user.getName();
    var optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    var match = optMatch.get();
    match.onReceivePlayerAction(playerId, new ActionReloadWeapon());
  }

  @EzyDoHandle(MatchCommand.TAKE_ITEM)
  public void handleTakeItem(EzyUser user) {
    var playerId = user.getName();
    var optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    var match = optMatch.get();
    match.onReceivePlayerAction(playerId, new ActionTakeItem());
  }

  @EzyDoHandle(MatchCommand.DROP_ITEM)
  public void handleDropItem(EzyUser user, PlayerDropItemRequest request) {
    var playerId = user.getName();
    var optMatch = matchingService.getMatchOfPlayer(playerId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return;
    }
    var match = optMatch.get();
    match.onReceivePlayerAction(playerId, new ActionDropItem(request.getItemId()));
  }
}
