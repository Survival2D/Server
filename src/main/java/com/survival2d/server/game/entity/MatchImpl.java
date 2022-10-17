package com.survival2d.server.game.entity;

import com.survival2d.server.constant.GameConstant;
import com.survival2d.server.game.action.PlayerAction;
import com.survival2d.server.game.action.PlayerAttack;
import com.survival2d.server.game.action.PlayerChangeWeapon;
import com.survival2d.server.game.action.PlayerMove;
import com.survival2d.server.game.entity.base.MapObject;
import com.survival2d.server.network.match.MatchCommand;
import com.survival2d.server.network.match.response.PlayerMoveResponse;
import com.survival2d.server.util.EzyFoxUtil;
import com.survival2d.server.util.vector.VectorUtil;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.locationtech.jts.math.Vector2D;


@Getter
@Slf4j
public class MatchImpl implements Match {

  private final long id;
  private final Map<Long, MapObject> objects = new ConcurrentHashMap<>();
  @Deprecated
  private final Map<Long, MatchTeam> teams = new ConcurrentHashMap<>();
  @Deprecated
  private final Map<String, Long> playerIdToTeam = new ConcurrentHashMap<>();

  private final Map<String, Map<Class<? extends PlayerAction>, PlayerAction>> playerRequests = new ConcurrentHashMap<>();
  private final Map<String, Player> players = new ConcurrentHashMap<>();
  private Timer timer;
  private TimerTask timerTask;
  private long currentTick;

  public MatchImpl(long id) {
    this.id = id;
  }

  @Override
  public void addPlayer(long teamId, String playerId) {
    players.putIfAbsent(playerId, new PlayerImpl(playerId, teamId));
  }

  @Override
  public Collection<String> getAllPlayers() {
    return players.keySet();
  }

  @Override
  public void onReceivePlayerAction(String playerId, PlayerAction action) {
    val playerActionMap = playerRequests.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
    playerActionMap.put(action.getClass(), action);
  }

  @Override
  public void onPlayerMove(String playerId, Vector2D direction, double rotation) {
    val player = players.get(playerId);
    if (player == null) {
      log.error("player {} is null", playerId);
      return;
    }
    if (!VectorUtil.isZero(direction)) {
      val unitDirection = direction.normalize();
      val moveBy = unitDirection.multiply(player.getSpeed());
      player.moveBy(moveBy);
    }
    player.setRotation(rotation);
    EzyFoxUtil.getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.PLAYER_MOVE)
        .data(
            PlayerMoveResponse.builder()
                .username(player.getPlayerId())
                .position(player.getPosition())
                .rotation(player.getRotation())
                .build())
        .usernames(getAllPlayers())
        .execute();
  }

  public void onPlayerSwitchWeapon(String playerId, int weaponId) {
    val player = players.get(playerId);
    if (player == null) {
      log.error("player {} is null", playerId);
      return;
    }
    player.switchWeapon(weaponId);
  }

  private void start() {
    timer = new Timer();
    timerTask = new TimerTask() {
      @Override
      public void run() {
        update();
      }
    };
    timer.scheduleAtFixedRate(timerTask, 0, GameConstant.PERIOD_PER_TICK);
    sendMatchStart();
  }

  private void sendMatchStart() {
    EzyFoxUtil.getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.MATCH_START)
        .usernames(getAllPlayers())
        .execute();
  }

  public void end() {
    timerTask.cancel();
    timer.cancel();
  }

  public void update() {
    currentTick++;
    updatePlayers();
  }

  private void updatePlayers() {
    for (val player : players.values()) {
      val playerActionMap = playerRequests.get(player.getPlayerId());
      if (playerActionMap == null) {
        continue;
      }
      for (val action : playerActionMap.values()) {
        handlePlayerAction(player.getPlayerId(), action);
      }
    }

  }

  private void handlePlayerAction(String playerId, PlayerAction action) {
    if (action instanceof PlayerMove) {
      val playerMove = (PlayerMove) action;
      onPlayerMove(playerId, playerMove.getDirection(), playerMove.getRotation());
    } else if (action instanceof PlayerAttack) {
      val playerAttack = (PlayerAttack) action;
      //TODO
    } else if (action instanceof PlayerChangeWeapon) {
      val playerChangeWeapon = (PlayerChangeWeapon) action;
      onPlayerSwitchWeapon(playerId, playerChangeWeapon.getWeaponIndex());
    }
  }
}
