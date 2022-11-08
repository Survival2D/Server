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
import com.survival2d.server.network.match.response.PlayerChangeWeaponResponse;
import com.survival2d.server.network.match.response.PlayerMoveResponse;
import com.survival2d.server.network.match.response.PlayerTakeDamageResponse;
import com.survival2d.server.util.EzyFoxUtil;
import com.survival2d.server.util.math.VectorUtil;
import com.survival2d.server.util.serialize.ExcludeFromGson;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
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
  @ExcludeFromGson private final Timer timer = new Timer();
  private long currentMapObjectId;
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
    val player = players.get(playerId);
    if (player.isDead()) {
      log.error("Player {} take action while dead", playerId);
      return;
    }
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
      createDamage(playerId, player.getPosition(), 10, 10);
    } else if (currentWeapon instanceof RangeWeapon) {
      createBullet(playerId, player.getPosition(), direction, BulletType.NORMAL);
    }
  }

  public void addMapObject(MapObject mapObject) {
    mapObject.setId(currentMapObjectId++);
    objects.put(mapObject.getId(), mapObject);
  }

  @Override
  public void createDamage(String playerId, Vector2D position, double radius, double damage) {
    val player = players.get(playerId);
    EzyFoxUtil.getInstance()
        .getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.PLAYER_ATTACK)
        .data(
            PlayerAttackResponse.builder()
                .username(playerId)
                .position(position)
                .weapon(player.getCurrentWeapon().get())
                .build())
        .usernames(getAllPlayers())
        .execute();
    makeDamage(playerId, position, radius, damage);
  }

  @Override
  public void makeDamage(String playerId, Vector2D position, double radius, double damage) {
    val currentPlayer = players.get(playerId);
    for (val player : players.values()) {
      if (player.getTeam() == currentPlayer.getTeam()) continue;
      if (player.isDead()) continue;
      if (VectorUtil.isCollision(player.getPosition(), position, player.getSize() + radius)) {
        player.takeDamage(damage);
        EzyFoxUtil.getInstance()
            .getResponseFactory()
            .newObjectResponse()
            .command(MatchCommand.PLAYER_TAKE_DAMAGE)
            .data(
                PlayerTakeDamageResponse.builder()
                    .playerId(player.getPlayerId())
                    .remainingHealth(player.getHealthPoint())
                    .build())
            .usernames(getAllPlayers())
            .execute();
        if (player.isDead()) {
          EzyFoxUtil.getInstance()
              .getResponseFactory()
              .newObjectResponse()
              .command(MatchCommand.PLAYER_DEAD)
              .data(player.getPlayerId())
              .usernames(getAllPlayers())
              .execute();
        }
      }
    }
  }

  @Override
  public void createBullet(
      String playerId, Vector2D position, Vector2D direction, BulletType type) {
    val bullet = new Bullet(playerId, position, direction, type);
    addMapObject(bullet);
    EzyFoxUtil.getInstance()
        .getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.CREATE_BULLET)
        .data(CreateBulletResponse.builder().bullet(bullet).build())
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
    EzyFoxUtil.getInstance()
        .getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.PLAYER_CHANGE_WEAPON)
        .data(
            PlayerChangeWeaponResponse.builder()
                .username(playerId)
                .weapon(player.getCurrentWeapon().get())
                .build())
        .usernames(getAllPlayers())
        .execute();
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
        var isDestroy = bullet.isOutOfBound();
        for (val player : players.values()) {
          if (VectorUtil.isCollision(
              player.getPosition(), bullet.getPosition(), player.getSize())) {
            makeDamage(
                bullet.getPlayerId(),
                bullet.getPosition(),
                bullet.getType().getDamageRadius(),
                bullet.getType().getDamageRadius());
          }
        }
        for (val otherObject : objects.values()) {
          if (otherObject == mapObject) continue; // Chính nó
          if (VectorUtil.isCollision(
              otherObject.getPosition(), bullet.getPosition(), 10 /*FIXME*/)) {
            makeDamage(
                bullet.getPlayerId(),
                bullet.getPosition(),
                bullet.getType().getDamageRadius(),
                bullet.getType().getDamageRadius());
          }
        }

        if (isDestroy) {
          objects.remove(bullet.getId());
          log.debug("bullet {} is destroyed", bullet.getId());
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
      val player = players.get(playerId);
      onPlayerAttach(playerId, player.getAttackDirection());
    } else if (action instanceof PlayerChangeWeapon) {
      val playerChangeWeapon = (PlayerChangeWeapon) action;
      onPlayerSwitchWeapon(playerId, playerChangeWeapon.getWeaponIndex());
    }
  }
}
