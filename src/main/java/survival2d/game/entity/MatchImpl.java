package survival2d.game.entity;

import com.google.flatbuffers.FlatBufferBuilder;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfoxserver.EzyZone;
import com.tvd12.ezyfoxserver.context.EzyZoneContext;
import com.tvd12.ezyfoxserver.entity.EzySession;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.wrapper.EzyZoneUserManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.RandomUtils;
import org.locationtech.jts.math.Vector2D;
import survival2d.ServerStartup;
import survival2d.constant.GameConstant;
import survival2d.flatbuffers.MapObjectData;
import survival2d.flatbuffers.MatchInfoResponse;
import survival2d.flatbuffers.Packet;
import survival2d.flatbuffers.PacketData;
import survival2d.flatbuffers.Vec2;
import survival2d.game.action.PlayerAction;
import survival2d.game.action.PlayerAttack;
import survival2d.game.action.PlayerChangeWeapon;
import survival2d.game.action.PlayerDropItem;
import survival2d.game.action.PlayerMove;
import survival2d.game.action.PlayerReloadWeapon;
import survival2d.game.action.PlayerTakeItem;
import survival2d.game.constant.GameConstants;
import survival2d.game.entity.base.Circle;
import survival2d.game.entity.base.Item;
import survival2d.game.entity.base.MapObject;
import survival2d.game.entity.base.Rectangle;
import survival2d.game.entity.base.Shape;
import survival2d.game.entity.config.BulletType;
import survival2d.game.entity.config.GunType;
import survival2d.game.entity.item.BulletItem;
import survival2d.game.entity.item.GunItem;
import survival2d.game.entity.obstacle.Container;
import survival2d.game.entity.obstacle.Obstacle;
import survival2d.game.entity.obstacle.Tree;
import survival2d.game.entity.weapon.Containable;
import survival2d.network.ByteBufferUtil;
import survival2d.network.match.MatchCommand;
import survival2d.network.match.response.CreateBulletResponse;
import survival2d.network.match.response.CreateItemResponse;
import survival2d.network.match.response.EndGameResponse;
import survival2d.network.match.response.ObstacleDestroyedResponse;
import survival2d.network.match.response.ObstacleTakeDamageResponse;
import survival2d.network.match.response.PlayerAttackResponse;
import survival2d.network.match.response.PlayerChangeWeaponResponse;
import survival2d.network.match.response.PlayerDeadResponse;
import survival2d.network.match.response.PlayerMoveResponse;
import survival2d.network.match.response.PlayerReloadWeaponResponse;
import survival2d.network.match.response.PlayerTakeDamageResponse;
import survival2d.network.match.response.PlayerTakeItemResponse;
import survival2d.service.MatchingService;
import survival2d.util.EzyFoxUtil;
import survival2d.util.math.VectorUtil;
import survival2d.util.serialize.ExcludeFromGson;

@Getter
@Slf4j
public class MatchImpl implements Match {

  private final long id;
  private final Map<Integer, MapObject> objects = new ConcurrentHashMap<>();

  @ExcludeFromGson
  private final Map<String, Map<Class<? extends PlayerAction>, PlayerAction>> playerRequests =
      new ConcurrentHashMap<>();

  private final Map<String, Player> players = new ConcurrentHashMap<>();
  @ExcludeFromGson private final Timer timer = new Timer();
  private int currentMapObjectId;
  @ExcludeFromGson private TimerTask gameLoopTask;
  private long currentTick;
  @ExcludeFromGson @EzyAutoBind private MatchingService matchingService;

  @ExcludeFromGson private EzyZoneContext zoneContext;

  public MatchImpl(long id) {
    this.id = id;
    init();
  }

  @Override
  public void addPlayer(long teamId, String playerId) {
    players.putIfAbsent(playerId, new PlayerImpl(playerId, teamId));
    int tryCount = 0;
    while (!randomPositionForPlayer(playerId)) {
      tryCount++;
      if (tryCount > 100) {
        log.error("Can't find position for player {}", playerId);
        break;
      }
    }
    playerRequests.put(playerId, new ConcurrentHashMap<>());
  }

  public boolean randomPositionForPlayer(String playerId) {
    val player = players.get(playerId);
    val newPosition =
        new Vector2D(RandomUtils.nextDouble(100, 900), RandomUtils.nextDouble(100, 900));
    player.setPosition(newPosition);
    for (val object : objects.values()) {
      if (object instanceof Obstacle) {
        val obstacle = (Obstacle) object;
        if (VectorUtil.isCollision(
            player.getPosition(), player.getShape(), obstacle.getPosition(), obstacle.getShape())) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public Collection<String> getAllPlayers() {
    return players.keySet();
  }

  @Override
  public void onReceivePlayerAction(String playerId, PlayerAction action) {
    val player = players.get(playerId);
    if (player.isDestroyed()) {
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
  public void onPlayerAttack(String playerId, Vector2D direction) {
    val player = players.get(playerId);
    val currentWeapon = player.getCurrentWeapon().get();
    if (currentWeapon.getAttachType() == AttachType.MELEE) {
      createDamage(
          playerId,
          player
              .getPosition()
              .add(player.getAttackDirection().multiply(((Circle) player.getShape()).getRadius())),
          new Circle(10),
          5);
    } else if (currentWeapon.getAttachType() == AttachType.RANGE) {
      createBullet(
          playerId,
          player
              .getPosition()
              .add(
                  player
                      .getAttackDirection()
                      .multiply(
                          ((Circle) player.getShape()).getRadius()
                              + GameConstants.INITIAL_BULLET_DISTANCE)),
          direction,
          BulletType.NORMAL);
    }
  }

  public void addMapObject(MapObject mapObject) {
    mapObject.setId(currentMapObjectId++);
    objects.put(mapObject.getId(), mapObject);
  }

  @Override
  public void createDamage(String playerId, Vector2D position, Shape shape, double damage) {
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
    makeDamage(playerId, position, shape, damage);
  }

  @Override
  public void makeDamage(String playerId, Vector2D position, Shape shape, double damage) {
    val currentPlayer = players.get(playerId);
    for (val player : players.values()) {
      if (player.getTeam() == currentPlayer.getTeam()) {
        continue;
      }
      if (player.isDestroyed()) {
        continue;
      }
      if (VectorUtil.isCollision(player.getPosition(), player.getShape(), position, shape)) {
        player.reduceHp(damage);
        EzyFoxUtil.getInstance()
            .getResponseFactory()
            .newObjectResponse()
            .command(MatchCommand.PLAYER_TAKE_DAMAGE)
            .data(
                PlayerTakeDamageResponse.builder()
                    .username(player.getPlayerId())
                    .hp(player.getHp())
                    .build())
            .usernames(getAllPlayers())
            .execute();
        if (player.isDestroyed()) {
          EzyFoxUtil.getInstance()
              .getResponseFactory()
              .newObjectResponse()
              .command(MatchCommand.PLAYER_DEAD)
              .data(PlayerDeadResponse.builder().username(player.getPlayerId()).build())
              .usernames(getAllPlayers())
              .execute();
          checkEndGame();
        }
      }
    }
    for (val object : objects.values()) {
      if (!(object instanceof Obstacle)) {
        continue;
      }
      val obstacle = (Obstacle) object;
      if (obstacle.isDestroyed()) {
        continue;
      }
      if (VectorUtil.isCollision(obstacle.getPosition(), obstacle.getShape(), position, shape)) {
        obstacle.reduceHp(damage);
        log.info(
            "Obstacle {} take damage {}, remainHp {}", obstacle.getId(), damage, obstacle.getHp());
        EzyFoxUtil.getInstance()
            .getResponseFactory()
            .newObjectResponse()
            .command(MatchCommand.OBSTACLE_TAKE_DAMAGE)
            .data(
                ObstacleTakeDamageResponse.builder()
                    .obstacleId(obstacle.getId())
                    .hp(obstacle.getHp())
                    .build())
            .usernames(getAllPlayers())
            .execute();
        if (obstacle.isDestroyed()) {
          log.info("Obstacle {} destroyed", obstacle.getId());
          EzyFoxUtil.getInstance()
              .getResponseFactory()
              .newObjectResponse()
              .command(MatchCommand.OBSTACLE_DESTROYED)
              .data(ObstacleDestroyedResponse.builder().obstacleId(obstacle.getId()).build())
              .usernames(getAllPlayers())
              .execute();
          if (obstacle instanceof Containable) {
            val containable = (Containable) obstacle;
            for (val item : containable.getItems()) {
              createItemOnMap(item, obstacle.getPosition());
            }
          }
        }
      }
    }
  }

  private void checkEndGame() {
    val isEnd =
        players.values().stream().filter(Player::isAlive).map(Player::getTeam).distinct().count()
            == 1; // Chỉ còn 1 team sống sót
    if (isEnd) {
      val winTeam =
          players.values().stream()
              .filter(Player::isAlive)
              .map(Player::getTeam)
              .distinct()
              .findFirst()
              .get();
      EzyFoxUtil.getInstance()
          .getResponseFactory()
          .newObjectResponse()
          .command(MatchCommand.END_GAME)
          .data(EndGameResponse.builder().winTeam(winTeam).build())
          .usernames(getAllPlayers())
          .execute();
      stop();
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

  @Override
  public void responseMatchInfo(String username) {
    val builder = new FlatBufferBuilder(0);

    int[] playerOffsets = new int[players.size()];
    val players = this.players.values().toArray(new Player[0]);
    for (int i = 0; i < players.length; i++) {
      val player = players[i];
      val usernameOffset = builder.createString(player.getPlayerId());
      val positionOffset =
          Vec2.createVec2(builder, player.getPosition().getX(), player.getPosition().getY());
      survival2d.flatbuffers.Player.startPlayer(builder);
      survival2d.flatbuffers.Player.addUsername(builder, usernameOffset);
      survival2d.flatbuffers.Player.addPosition(builder, positionOffset);
      survival2d.flatbuffers.Player.addRotation(builder, player.getRotation());
      playerOffsets[i] = survival2d.flatbuffers.Player.endPlayer(builder);
    }

    int[] mapObjectOffsets = new int[objects.size()];
    val mapObjects = this.objects.values().toArray(new MapObject[0]);
    for (int i = 0; i < mapObjects.length; i++) {
      val object = mapObjects[i];
      val positionOffset =
          Vec2.createVec2(builder, object.getPosition().getX(), object.getPosition().getY());
      survival2d.flatbuffers.MapObject.startMapObject(builder);
      survival2d.flatbuffers.MapObject.addId(builder, object.getId());
      survival2d.flatbuffers.MapObject.addPosition(builder, positionOffset);
      if (object instanceof BulletItem) {
        val bulletItem = (BulletItem) object;
        survival2d.flatbuffers.MapObject.addDataType(builder, MapObjectData.BulletItem);
        survival2d.flatbuffers.BulletItem.startBulletItem(builder);
        survival2d.flatbuffers.BulletItem.addType(
            builder, (byte) bulletItem.getBulletType().ordinal());
        val bulletItemOffset = survival2d.flatbuffers.BulletItem.endBulletItem(builder);
        survival2d.flatbuffers.MapObject.addData(builder, bulletItemOffset);
      } else if (object instanceof GunItem) {
        val gunItem = (GunItem) object;
        survival2d.flatbuffers.MapObject.addDataType(builder, MapObjectData.GunItem);
        survival2d.flatbuffers.GunItem.startGunItem(builder);
        survival2d.flatbuffers.GunItem.addType(builder, (byte) gunItem.getGunType().ordinal());
        val gunItemOffset = survival2d.flatbuffers.GunItem.endGunItem(builder);
        survival2d.flatbuffers.MapObject.addData(builder, gunItemOffset);
      } else if (object instanceof Tree) {
        val tree = (Tree) object;
        survival2d.flatbuffers.MapObject.addDataType(builder, MapObjectData.Tree);
        survival2d.flatbuffers.Tree.startTree(builder);
        val treeOffset = survival2d.flatbuffers.Tree.endTree(builder);
        survival2d.flatbuffers.MapObject.addData(builder, treeOffset);
      } else if (object instanceof Container) {
        val container = (Container) object;
        survival2d.flatbuffers.MapObject.addDataType(builder, MapObjectData.Container);
        survival2d.flatbuffers.Container.startContainer(builder);
        val containerOffset = survival2d.flatbuffers.Container.endContainer(builder);
        survival2d.flatbuffers.MapObject.addData(builder, containerOffset);
      }
      // TODO: add more map object
      mapObjectOffsets[i] = survival2d.flatbuffers.MapObject.endMapObject(builder);
    }

    val playersOffset = MatchInfoResponse.createPlayersVector(builder, playerOffsets);
    val mapObjectsOffset = MatchInfoResponse.createMapObjectsVector(builder, mapObjectOffsets);


    MatchInfoResponse.startMatchInfoResponse(builder);
    MatchInfoResponse.addPlayers(builder, playersOffset);
    MatchInfoResponse.addMapObjects(builder, mapObjectsOffset);
    val responseOffset = MatchInfoResponse.endMatchInfoResponse(builder);

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.MatchInfoResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    zoneContext.stream(bytes, getSession(username));
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
                .slot(player.getCurrentWeaponIndex())
                .weapon(player.getCurrentWeapon().get())
                .build())
        .usernames(getAllPlayers())
        .execute();
  }

  public void init() {
    zoneContext = ServerStartup.getServerContext().getZoneContext(ServerStartup.ZONE_NAME);

    initObstacles();
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            start();
          }
        },
        3000);
  }

  private EzyZone getZone() {
    return zoneContext.getZone();
  }

  private EzyZoneUserManager getZoneUserManager() {
    return getZone().getUserManager();
  }

  private EzyUser getUser(String username) {
    return getZoneUserManager().getUser(username);
  }

  private EzySession getSession(String username) {
    return getUser(username).getSession();
  }

  private List<EzySession> getSessions(List<String> usernames) {
    return usernames.stream().map(this::getSession).collect(Collectors.toList());
  }

  public void stop() {
    timer.cancel();
    //    EzyFoxUtil.getInstance().getMatchingService().destroyMatch(this.getId());
  }

  public boolean randomPositionForObstacle(Obstacle obstacle) {
    val newPosition =
        new Vector2D(RandomUtils.nextDouble(100, 900), RandomUtils.nextDouble(100, 900));
    obstacle.setPosition(newPosition);
    for (val player : players.values()) {
      if (VectorUtil.isCollision(
          player.getPosition(), player.getShape(), newPosition, obstacle.getShape())) {
        return false;
      }
    }
    for (val object : objects.values()) {
      if (object instanceof Obstacle) {
        val otherObstacle = (Obstacle) object;
        if (VectorUtil.isCollision(
            otherObstacle.getPosition(),
            otherObstacle.getShape(),
            newPosition,
            obstacle.getShape())) {
          return false;
        }
      }
    }
    return true;
  }

  private void initObstacles() {
    // TODO: random this
    for (int i = 0; i < 6; i++) {
      val tree = new Tree();
      tree.setShape(new Circle(35));
      int tryTime = 0;
      while (!randomPositionForObstacle(tree)) {
        tryTime++;
        if (tryTime > 100) {
          log.error("Can't random position for tree {}", tree.getId());
          break;
        }
      }
      addMapObject(tree);
    }

    for (int i = 0; i < 7; i++) {

      val container = new Container();
      container.setShape(new Rectangle(100, 100));
      container.setItems(
          Arrays.asList(
              GunItem.builder().gunType(GunType.NORMAL).numBullet(10).build(),
              BulletItem.builder().bulletType(BulletType.NORMAL).numBullet(10).build()));
      int tryTime = 0;
      while (!randomPositionForObstacle(container)) {
        tryTime++;
        if (tryTime > 100) {
          log.error("Can't random position for container {}", container.getId());
          break;
        }
      }
      addMapObject(container);
    }
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
        log.info("bullet position {}", bullet.getPosition());
        String ownerId = bullet.getOwnerId();
        val owner = players.get(ownerId);
        for (val player : players.values()) {
          log.info("player's team {}, owner's team {}", player.getTeam(), owner.getTeam());
          log.info("player's hp {}, player is dead {}", player.getHp(), player.isDestroyed());
          if (player.getTeam() == owner.getTeam() || player.isDestroyed()) {
            continue;
          }
          log.info("player position {}", player.getPosition());
          if (VectorUtil.isCollision(
              player.getPosition(), player.getShape(), bullet.getPosition(), bullet.getShape())) {
            log.info("player {} is hit by bullet {}", player.getPlayerId(), bullet.getId());
            makeDamage(
                ownerId,
                bullet.getPosition(),
                new Circle(bullet.getType().getDamageRadius()),
                bullet.getType().getDamage());
            bullet.setDestroyed(true);
          }
        }
        for (val otherObject : objects.values()) {
          if (otherObject == mapObject) {
            continue; // Chính nó
          }
          if (otherObject instanceof Obstacle) {
            val obstacle = (Obstacle) otherObject;
            if (obstacle.isDestroyed()) {
              continue;
            }
          }
          if (VectorUtil.isCollision(
              otherObject.getPosition(),
              otherObject.getShape(),
              bullet.getPosition(),
              bullet.getShape())) {
            log.info("object {} is hit by bullet {}", otherObject.getId(), bullet.getId());
            makeDamage(
                ownerId,
                bullet.getPosition(),
                new Circle(bullet.getType().getDamageRadius()),
                bullet.getType().getDamageRadius());
            bullet.setDestroyed(true);
          }
        }

        var isDestroy = bullet.isDestroyed() || bullet.isOutOfBound();
        if (isDestroy) {
          objects.remove(bullet.getId());
          log.info("bullet {} is destroyed", bullet.getId());
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
        // TODO: nếu mà client chưa sửa kịp, thì comment 3 dòng sau
        if (!(action instanceof PlayerMove)) {
          playerActionMap.remove(action.getClass());
        }
      }
      //      playerActionMap.clear();
    }
  }

  private void handlePlayerAction(String playerId, PlayerAction action) {
    if (action instanceof PlayerMove) {
      val playerMove = (PlayerMove) action;
      onPlayerMove(playerId, playerMove.getDirection(), playerMove.getRotation());
    } else if (action instanceof PlayerAttack) {
      val player = players.get(playerId);
      onPlayerAttack(playerId, player.getAttackDirection());
    } else if (action instanceof PlayerChangeWeapon) {
      val playerChangeWeapon = (PlayerChangeWeapon) action;
      onPlayerSwitchWeapon(playerId, playerChangeWeapon.getWeaponIndex());
    } else if (action instanceof PlayerReloadWeapon) {
      onPlayerReloadWeapon(playerId);
    } else if (action instanceof PlayerTakeItem) {
      onPlayerTakeItem(playerId);
    } else if (action instanceof PlayerDropItem) {
      val playerDropItem = (PlayerDropItem) action;
      onPlayerDropItem(playerId, playerDropItem.getItemId());
    }
  }

  private void createItemOnMap(Item item, Vector2D position) {
    log.info("create {} on map", item.getItemType());
    val randomNeighborPosition =
        new Vector2D(RandomUtils.nextDouble(0, 20) - 10, RandomUtils.nextDouble(0, 20) - 10);
    val itemOnMap =
        ItemOnMap.builder().item(item).position(position.add(randomNeighborPosition)).build();
    addMapObject(itemOnMap);
    EzyFoxUtil.getInstance()
        .getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.CREATE_ITEM)
        .data(CreateItemResponse.builder().item(itemOnMap).build())
        .usernames(getAllPlayers())
        .execute();
  }

  private void onPlayerTakeItem(String playerId) {
    val player = players.get(playerId);
    for (val object : objects.values()) {
      if (!(object instanceof ItemOnMap)) {
        continue;
      }
      val itemOnMap = (ItemOnMap) object;
      if (VectorUtil.isCollision(
          player.getPosition(), player.getShape(), itemOnMap.getPosition(), itemOnMap.getShape())) {
        //        player.addItem(itemOnMap.getItem()); //TODO: add item to player
        objects.remove(itemOnMap.getId());
        EzyFoxUtil.getInstance()
            .getResponseFactory()
            .newObjectResponse()
            .command(MatchCommand.TAKE_ITEM)
            .data(PlayerTakeItemResponse.builder().username(playerId).item(itemOnMap).build())
            .usernames(getAllPlayers())
            .execute();
      }
    }
  }

  private void onPlayerReloadWeapon(String playerId) {
    val player = players.get(playerId);
    player.reloadWeapon();
    EzyFoxUtil.getInstance()
        .getResponseFactory()
        .newObjectResponse()
        .command(MatchCommand.PLAYER_RELOAD)
        .data(PlayerReloadWeaponResponse.builder().player(player).build())
        .username(playerId)
        .execute();
  }

  private void onPlayerDropItem(String playerId, String itemId) {
    // TODO
  }
}
