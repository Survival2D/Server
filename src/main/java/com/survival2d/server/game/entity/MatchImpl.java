package com.survival2d.server.game.entity;

import com.survival2d.server.constant.GameConstant;
import com.survival2d.server.game.action.PlayerAction;
import com.survival2d.server.game.action.PlayerAttack;
import com.survival2d.server.game.action.PlayerChangeWeapon;
import com.survival2d.server.game.action.PlayerMove;
import com.survival2d.server.game.entity.base.MapObject;
import com.survival2d.server.game.entity.config.BulletType;
import com.survival2d.server.game.entity.weapon.MeleeWeapon;
import com.survival2d.server.game.entity.weapon.RangeWeapon;
import com.survival2d.server.network.match.MatchCommand;
import com.survival2d.server.network.match.response.CreateBulletResponse;
import com.survival2d.server.network.match.response.PlayerAttackResponse;
import com.survival2d.server.network.match.response.PlayerMoveResponse;
import com.survival2d.server.util.EzyFoxUtil;
import com.survival2d.server.util.serialize.ExcludeFromGson;
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
  @ExcludeFromGson private final Map<Long, MapObject> objects = new ConcurrentHashMap<>();
  @ExcludeFromGson @Deprecated private final Map<Long, MatchTeam> teams = new ConcurrentHashMap<>();

  @ExcludeFromGson @Deprecated
  private final Map<String, Long> playerIdToTeam = new ConcurrentHashMap<>();

  @ExcludeFromGson
  private final Map<String, Map<Class<? extends PlayerAction>, PlayerAction>> playerRequests =
      new ConcurrentHashMap<>();

  private final Map<String, Player> players = new ConcurrentHashMap<>();
  private long currentMapObjectId;
  @ExcludeFromGson private Timer timer = new Timer();
  @ExcludeFromGson private TimerTask gameLoopTask;
  private long currentTick;

  public MatchImpl(long id) {
    this.id = id;
    init();
  }

  @Override
  public void addPlayer(long teamId, String playerId) {
    players.putIfAbsent(playerId, new PlayerImpl(playerId, teamId));
    playerRequests.put(playerId, new ConcurrentHashMap<>());
  }

  @Override
  public Collection<String> getAllPlayers() {
    return players.keySet();
  }

  @Override
  public void onReceivePlayerAction(String playerId, PlayerAction action) {
    val playerActionMap = playerRequests.get(playerId);
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
    EzyFoxUtil.getInstance()
        .getResponseFactory()
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

  @Override
  public void onPlayerAttach(String playerId, Vector2D direction) {
    val player = players.get(playerId);
    val currentWeapon = player.getCurrentWeapon().get();
    if (currentWeapon instanceof MeleeWeapon) {
      createDamage(player.getPosition(), 10, 10);
    } else if (currentWeapon instanceof RangeWeapon) {
      createBullet(player.getPosition(), direction, BulletType.NORMAL);
    }
  }

  public void addMapObject(MapObject mapObject) {
    mapObject.setId(currentMapObjectId++);
    objects.put(mapObject.getId(), mapObject);
  }

  @Override
  public void createDamage(Vector2D position, double radius, double damage) {
    EzyFoxUtil.getInstance()
        .getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.PLAYER_ATTACK)
        .data(PlayerAttackResponse.builder().build())//TODO
        .usernames(getAllPlayers())
        .execute();
  }

  @Override
  public void createBullet(Vector2D position, Vector2D direction, BulletType type) {
    val bullet = new Bullet(position, direction, type);
    addMapObject(bullet);
    EzyFoxUtil.getInstance()
        .getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.CREATE_BULLET)
        .data(CreateBulletResponse.builder().build())//TODO
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

  public void init() {
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            start();
          }
        },
        3000);
  }

  public void start() {
    sendMatchStart();
    gameLoopTask =
        new TimerTask() {
          @Override
          public void run() {
            update();
          }
        };
    timer.scheduleAtFixedRate(gameLoopTask, 0, GameConstant.PERIOD_PER_TICK);
  }

  private void sendMatchStart() {
    EzyFoxUtil.getInstance()
        .getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.MATCH_START)
        .usernames(getAllPlayers())
        .execute();
  }

  public void end() {
    gameLoopTask.cancel();
    timer.cancel();
  }

  public void update() {
    currentTick++;
    updatePlayers();
    updateMapObjects();
  }

  private void updateMapObjects() {
    for (val mapObject : objects.values()) {
      if (mapObject instanceof Bullet) {
        val bullet = (Bullet) mapObject;
        bullet.move();
        if (bullet.isDestroyed()) {
          objects.remove(bullet.getId());
        }
      }
    }
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
      playerActionMap.clear();
    }
  }

  private void handlePlayerAction(String playerId, PlayerAction action) {
    if (action instanceof PlayerMove) {
      val playerMove = (PlayerMove) action;
      onPlayerMove(playerId, playerMove.getDirection(), playerMove.getRotation());
    } else if (action instanceof PlayerAttack) {
      val playerAttack = (PlayerAttack) action;
      onPlayerAttach(playerId, playerAttack.getDirection());
    } else if (action instanceof PlayerChangeWeapon) {
      val playerChangeWeapon = (PlayerChangeWeapon) action;
      onPlayerSwitchWeapon(playerId, playerChangeWeapon.getWeaponIndex());
    }
  }
}
