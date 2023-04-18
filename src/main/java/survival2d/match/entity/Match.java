package survival2d.match.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.google.flatbuffers.ByteBufferUtil;
import com.google.flatbuffers.FlatBufferBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import survival2d.flatbuffers.BulletItemTable;
import survival2d.flatbuffers.BulletTable;
import survival2d.flatbuffers.ContainerTable;
import survival2d.flatbuffers.CreateBulletOnMapResponse;
import survival2d.flatbuffers.CreateItemOnMapResponse;
import survival2d.flatbuffers.EndGameResponse;
import survival2d.flatbuffers.GunItemTable;
import survival2d.flatbuffers.ItemUnion;
import survival2d.flatbuffers.MapObjectTable;
import survival2d.flatbuffers.MapObjectUnion;
import survival2d.flatbuffers.MatchInfoResponse;
import survival2d.flatbuffers.NewSafeZoneResponse;
import survival2d.flatbuffers.ObstacleDestroyResponse;
import survival2d.flatbuffers.ObstacleTakeDamageResponse;
import survival2d.flatbuffers.PlayerAttackResponse;
import survival2d.flatbuffers.PlayerChangeWeaponResponse;
import survival2d.flatbuffers.PlayerDeadResponse;
import survival2d.flatbuffers.PlayerMoveResponse;
import survival2d.flatbuffers.PlayerReloadWeaponResponse;
import survival2d.flatbuffers.PlayerTable;
import survival2d.flatbuffers.PlayerTakeDamageResponse;
import survival2d.flatbuffers.PlayerTakeItemResponse;
import survival2d.flatbuffers.Response;
import survival2d.flatbuffers.ResponseUnion;
import survival2d.flatbuffers.SafeZoneMoveResponse;
import survival2d.flatbuffers.StoneTable;
import survival2d.flatbuffers.TreeTable;
import survival2d.flatbuffers.Vector2Struct;
import survival2d.flatbuffers.WallTable;
import survival2d.match.action.PlayerAction;
import survival2d.match.action.PlayerAttack;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerMove;
import survival2d.match.action.PlayerReloadWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.match.config.GameConfig;
import survival2d.match.constant.GameConstant;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.config.BulletType;
import survival2d.match.entity.config.GunType;
import survival2d.match.entity.item.BulletItem;
import survival2d.match.entity.item.GunItem;
import survival2d.match.entity.obstacle.Container;
import survival2d.match.entity.obstacle.Obstacle;
import survival2d.match.entity.obstacle.Stone;
import survival2d.match.entity.obstacle.Tree;
import survival2d.match.entity.obstacle.Wall;
import survival2d.match.entity.weapon.Containable;
import survival2d.match.util.MatchUtil;
import survival2d.util.serialize.GsonTransient;

@Getter
@Slf4j
public class Match {

  @GsonTransient
  private final long id;
  private final Map<Integer, MapObject> objects = new ConcurrentHashMap<>();

  @GsonTransient
  private final Map<String, Map<Class<? extends PlayerAction>, PlayerAction>> playerRequests =
      new ConcurrentHashMap<>();

  private final Map<String, Player> players = new ConcurrentHashMap<>();
  @GsonTransient
  private final Timer timer = new Timer();
  private final List<Circle> safeZones = new ArrayList<>();
  @GsonTransient
  int nextSafeZone;
  @GsonTransient
  private int currentMapObjectId;
  @GsonTransient
  private TimerTask gameLoopTask;
  @GsonTransient
  private long currentTick;

  public Match(long id) {
    this.id = id;
    init();
  }

  public void addPlayer(int teamId, int userId) {
    players.putIfAbsent(userId, new Player(userId, teamId));
    int tryCount = 0;
    while (!randomPositionForPlayer(userId)) {
      tryCount++;
      if (tryCount > 100) {
        log.error("Can't find position for player {}", userId);
        break;
      }
    }
    playerRequests.put(userId, new ConcurrentHashMap<>());
  }

  public boolean randomPositionForPlayer(String playerId) {
    var player = players.get(playerId);
    var newPosition = MatchUtil.randomPosition(100, 9900, 100, 9900);
    player.setPosition(newPosition);
    for (var object : objects.values()) {
      if (object instanceof Obstacle obstacle) {
        if (MatchUtil.isCollision(player.getShape(), obstacle.getShape())) {
          return false;
        }
      }
    }
    return true;
  }

  public Collection<String> getAllPlayers() {
    return players.keySet();
  }

  public void onReceivePlayerAction(String playerId, PlayerAction action) {
    var player = players.get(playerId);
    if (player.isDestroyed()) {
      log.error("Player {} take action while dead", playerId);
      return;
    }
    var playerActionMap = playerRequests.get(playerId);
    playerActionMap.put(action.getClass(), action);
  }

  public void onPlayerMove(String playerId, Vector2 direction, double rotation) {
    var player = players.get(playerId);
    if (player == null) {
      log.error("player {} is null", playerId);
      return;
    }
    if (direction.isZero()) {
      var unitDirection = direction.nor();
      var moveBy = unitDirection.scl(player.getSpeed());
      player.moveBy(moveBy);
    }
    player.setRotation(rotation);
    var builder = new FlatBufferBuilder(0);
    var usernameOffset = builder.createString(playerId);

    PlayerMoveResponse.startPlayerMoveResponse(builder);
    PlayerMoveResponse.addUsername(builder, usernameOffset);
    PlayerMoveResponse.addRotation(builder, rotation);
    var positionOffset = Vector2Struct.createVector2Struct(builder, direction.x, direction.y);
    PlayerMoveResponse.addPosition(builder, positionOffset);
    var responseOffset = PlayerMoveResponse.endPlayerMoveResponse(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.PlayerMoveResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    zoneContext.stream(bytes, getSessions(getAllPlayers()));
  }

  public void onPlayerAttack(String playerId, Vector2 direction) {
    var player = players.get(playerId);
    var currentWeapon = player.getCurrentWeapon().get();
    if (currentWeapon.getAttachType() == AttachType.MELEE) {
      createDamage(
          playerId,
          player
              .getPosition()
              .add(player.getAttackDirection().scl(((Circle) player.getShape()).radius)),
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
                      .scl(
                          ((Circle) player.getShape()).radius
                              + GameConstant.INITIAL_BULLET_DISTANCE)),
          direction,
          BulletType.NORMAL);
    }
  }

  public void addMapObject(MapObject mapObject) {
    mapObject.setId(currentMapObjectId++);
    objects.put(mapObject.getId(), mapObject);
  }

  public void createDamage(String playerId, Shape2D shape, double damage) {
    var player = players.get(playerId);
    log.warn("match is not present");
    var builder = new FlatBufferBuilder(0);
    var usernameOffset = builder.createString(playerId);

    PlayerAttackResponse.startPlayerAttackResponse(builder);
    PlayerAttackResponse.addUsername(builder, usernameOffset);
    var positionOffset = Vec2.createVec2(builder, position.getX(), position.getY());
    PlayerMoveResponse.addPosition(builder, positionOffset);
    var responseOffset = PlayerAttackResponse.endPlayerAttackResponse(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.PlayerAttackResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    zoneContext.stream(bytes, getSessions(getAllPlayers()));
    makeDamage(playerId, shape, damage);
  }

  public void makeDamage(String playerId, Shape2D shape, double damage) {
    var currentPlayer = players.get(playerId);
    for (var player : players.values()) {
      if (player.getTeam() == currentPlayer.getTeam()) {
        continue;
      }
      if (player.isDestroyed()) {
        continue;
      }
      if (MatchUtil.isCollision(player.getShape(), shape)) {
        var damageMultiple =
            MatchUtil.isCollision(player.getHead(), shape)
                ? GameConstant.HEADSHOT_DAMAGE
                : GameConstant.BODY_DAMAGE;
        player.reduceHp(damage * damageMultiple);
        {
          var builder = new FlatBufferBuilder(0);
          var usernameOffset = builder.createString(playerId);

          PlayerTakeDamageResponse.startPlayerTakeDamageResponse(builder);
          PlayerTakeDamageResponse.addUsername(builder, usernameOffset);
          PlayerTakeDamageResponse.addRemainHp(builder, player.getHp());
          var responseOffset = PlayerTakeDamageResponse.endPlayerTakeDamageResponse(builder);

          Response.startResponse(builder);
          Response.addResponseType(builder, ResponseUnion.PlayerTakeDamageResponse);
          Response.addResponse(builder, responseOffset);
          var packetOffset = Response.endResponse(builder);
          builder.finish(packetOffset);

          var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          zoneContext.stream(bytes, getSessions(getAllPlayers()));
        }
        if (player.isDestroyed()) {
          var builder = new FlatBufferBuilder(0);
          var usernameOffset = builder.createString(playerId);

          PlayerDeadResponse.startPlayerDeadResponse(builder);
          PlayerDeadResponse.addUsername(builder, usernameOffset);
          var responseOffset = PlayerDeadResponse.endPlayerDeadResponse(builder);

          Response.startResponse(builder);
          Response.addResponseType(builder, ResponseUnion.PlayerDeadResponse);
          Response.addResponse(builder, responseOffset);
          var packetOffset = Response.endResponse(builder);
          builder.finish(packetOffset);

          var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          zoneContext.stream(bytes, getSessions(getAllPlayers()));
          checkEndGame();
        }
      }
    }
    for (var object : objects.values()) {
      if (!(object instanceof Obstacle obstacle)) {
        continue;
      }
      if (!(obstacle instanceof Destroyable destroyable)) {
        continue;
      }
      if (destroyable.isDestroyed()) {
        continue;
      }
      var hasHp = (HasHp) obstacle;
      if (MatchUtil.isCollision(obstacle.getShape(), shape)) {
        hasHp.reduceHp(damage);
        log.info(
            "Obstacle {} take damage {}, remainHp {}", obstacle.getId(), damage, hasHp.getHp());
        {
          var builder = new FlatBufferBuilder(0);

          var responseOffset =
              ObstacleTakeDamageResponse.createObstacleTakeDamageResponse(
                  builder, obstacle.getId(), hasHp.getHp());

          Response.startResponse(builder);
          Response.addResponseType(builder, ResponseUnion.ObstacleTakeDamageResponse);
          Response.addResponse(builder, responseOffset);
          var packetOffset = Response.endResponse(builder);
          builder.finish(packetOffset);

          var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          zoneContext.stream(bytes, getSessions(getAllPlayers()));
        }
        if (destroyable.isDestroyed()) {
          log.info("Obstacle {} destroyed", obstacle.getId());
          var builder = new FlatBufferBuilder(0);

          var responseOffset =
              ObstacleDestroyResponse.createObstacleDestroyResponse(builder, obstacle.getId());

          Response.startResponse(builder);
          Response.addResponseType(builder, ResponseUnion.ObstacleDestroyResponse);
          Response.addResponse(builder, responseOffset);
          var packetOffset = Response.endResponse(builder);
          builder.finish(packetOffset);

          var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          zoneContext.stream(bytes, getSessions(getAllPlayers()));
          if (obstacle instanceof Containable containable) {
            for (var item : containable.getItems()) {
              createItemOnMap(
                  item,
                  obstacle.getPosition().add(MatchUtil.randomPosition(-10, 10, -10, 10)),
                  obstacle.getPosition());
            }
          }
        }
      }
    }
  }

  private void checkEndGame() {
    var isEnd =
        players.values().stream().filter(Player::isAlive).map(Player::getTeam).distinct().count()
            == 1; // Chỉ còn 1 team sống sót
    if (isEnd) {
      var winTeam =
          players.values().stream()
              .filter(Player::isAlive)
              .map(Player::getTeam)
              .distinct()
              .findFirst()
              .get();
      var builder = new FlatBufferBuilder(0);

      var responseOffset = EndGameResponse.createEndGameResponse(builder, winTeam);

      Response.startResponse(builder);
      Response.addResponseType(builder, ResponseUnion.EndGameResponse);
      Response.addResponse(builder, responseOffset);
      var packetOffset = Response.endResponse(builder);
      builder.finish(packetOffset);

      var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
      zoneContext.stream(bytes, getSessions(getAllPlayers()));
      stop();
    }
  }

  public void createBullet(String playerId, Vector2 position, Vector2 direction, BulletType type) {
    var bullet = new Bullet(playerId, position, direction, type);
    addMapObject(bullet);
    var builder = new FlatBufferBuilder(0);
    var usernameOffset = builder.createString(playerId);
    var positionOffset = Vector2Struct.createVector2Struct(builder, position.x, position.y);
    var directionOffset = Vector2Struct.createVector2Struct(builder, direction.x, direction.y);

    BulletTable.startBulletTable(builder);
    BulletTable.addType(builder, (byte) type.ordinal());
    BulletTable.addId(builder, bullet.getId());
    BulletTable.addPosition(builder, positionOffset);
    BulletTable.addDirection(builder, directionOffset);
    BulletTable.addOwner(builder, usernameOffset);
    var bulletOffset = BulletTable.endBulletTable(builder);
    var responseOffset =
        CreateBulletOnMapResponse.createCreateBulletOnMapResponse(builder, bulletOffset);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.CreateBulletOnMapResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    zoneContext.stream(bytes, getSessions(getAllPlayers()));
  }

  public void responseMatchInfo(String username) {
    final byte[] bytes = getMatchInfoPacket();
    zoneContext.stream(bytes, getSession(username));
  }

  private byte[] getMatchInfoPacket() {
    var builder = new FlatBufferBuilder(0);

    final int responseOffset = putResponseData(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.MatchInfoResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    return bytes;
  }

  public int putResponseData(FlatBufferBuilder builder) {
    int[] playerOffsets = new int[players.size()];
    var players = this.players.values().toArray(new Player[0]);
    for (int i = 0; i < players.length; i++) {
      var player = players[i];
      var usernameOffset = builder.createString(player.getPlayerId());
      PlayerTable.startPlayerTable(builder);
      PlayerTable.addUsername(builder, usernameOffset);
      var positionOffset =
          Vector2Struct.createVector2Struct(
              builder, player.getPosition().x, player.getPosition().y);
      PlayerTable.addPosition(builder, positionOffset);
      PlayerTable.addRotation(builder, player.getRotation());
      playerOffsets[i] = PlayerTable.endPlayerTable(builder);
    }

    int[] mapObjectOffsets = new int[objects.size()];
    var mapObjects = this.objects.values().toArray(new MapObject[0]);
    for (int i = 0; i < mapObjects.length; i++) {
      var object = mapObjects[i];
      var objectDataOffset = 0;
      byte objectDataType = 0;
      if (object instanceof BulletItem bulletItem) {
        objectDataType = MapObjectUnion.BulletItemTable;
        BulletItemTable.startBulletItemTable(builder);
        BulletItemTable.addType(builder, (byte) bulletItem.getBulletType().ordinal());
        var bulletItemOffset = BulletItemTable.endBulletItemTable(builder);
        MapObjectTable.addData(builder, bulletItemOffset);
      } else if (object instanceof GunItem gunItem) {
        objectDataType = MapObjectUnion.GunItemTable;
        GunItemTable.startGunItemTable(builder);
        GunItemTable.addType(builder, (byte) gunItem.getGunType().ordinal());
        var gunItemOffset = GunItemTable.endGunItemTable(builder);
        MapObjectTable.addData(builder, gunItemOffset);
      } else if (object instanceof Tree tree) {
        objectDataType = MapObjectUnion.TreeTable;
        TreeTable.startTreeTable(builder);
        var treeOffset = TreeTable.endTreeTable(builder);
        MapObjectTable.addData(builder, treeOffset);
      } else if (object instanceof Container container) {
        objectDataType = MapObjectUnion.ContainerTable;
        ContainerTable.startContainerTable(builder);
        var containerOffset = ContainerTable.endContainerTable(builder);
        MapObjectTable.addData(builder, containerOffset);
      } else if (object instanceof Stone stone) {
        objectDataType = MapObjectUnion.StoneTable;
        StoneTable.startStoneTable(builder);
        var stoneOffset = StoneTable.endStoneTable(builder);
        MapObjectTable.addData(builder, stoneOffset);
      } else if (object instanceof Wall wall) {
        objectDataType = MapObjectUnion.WallTable;
        WallTable.startWallTable(builder);
        var wallOffset = WallTable.endWallTable(builder);
        MapObjectTable.addData(builder, wallOffset);
      }
      // TODO: add more map object
      MapObjectTable.startMapObjectTable(builder);
      MapObjectTable.addId(builder, object.getId());
      var positionOffset =
          Vector2Struct.createVector2Struct(
              builder, object.getPosition().x, object.getPosition().y);
      MapObjectTable.addPosition(builder, positionOffset);
      MapObjectTable.addDataType(builder, objectDataType);
      MapObjectTable.addData(builder, objectDataOffset);
      mapObjectOffsets[i] = MapObjectTable.endMapObjectTable(builder);
    }

    var playersOffset = MatchInfoResponse.createPlayersVector(builder, playerOffsets);
    var mapObjectsOffset = MatchInfoResponse.createMapObjectsVector(builder, mapObjectOffsets);

    MatchInfoResponse.startMatchInfoResponse(builder);
    MatchInfoResponse.addPlayers(builder, playersOffset);
    MatchInfoResponse.addMapObjects(builder, mapObjectsOffset);
    var safeZoneOffset =
        Vector2Struct.createVector2Struct(builder, safeZones.get(0).x, safeZones.get(0).y);
    MatchInfoResponse.addSafeZone(builder, safeZoneOffset);
    var responseOffset = MatchInfoResponse.endMatchInfoResponse(builder);
    return responseOffset;
  }

  public void responseMatchInfo() {
    var bytes = getMatchInfoPacket();
    zoneContext.stream(bytes, getSessions(getAllPlayers()));
  }

  public void onPlayerSwitchWeapon(String playerId, int weaponId) {
    var player = players.get(playerId);
    if (player == null) {
      log.error("player {} is null", playerId);
      return;
    }
    player.switchWeapon(weaponId);
    var builder = new FlatBufferBuilder(0);
    var usernameOffset = builder.createString(playerId);

    var responseOffset =
        PlayerChangeWeaponResponse.createPlayerChangeWeaponResponse(
            builder, usernameOffset, (byte) weaponId);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.PlayerChangeWeaponResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    zoneContext.stream(bytes, getSessions(getAllPlayers()));
  }

  public void init() {
    //    zoneContext =
    // Survival2DStartup.getServerContext().getZoneContext(Survival2DStartup.ZONE_NAME);
    initSafeZones();
    initObstacles();
    //    timer.schedule(
    //        new TimerTask() {
    //
    //          public void run() {
    //            start();
    //          }
    //        },
    //        3000);
  }

  private void initSafeZones() {
    safeZones.add(
        new Circle(
            GameConfig.getInstance().getDefaultSafeZoneCenterX(),
            GameConfig.getInstance().getDefaultSafeZoneCenterY(),
            GameConfig.getInstance().getDefaultSafeZoneRadius()));
    for (var radius : GameConfig.getInstance().getSafeZonesRadius()) {
      var previousSafeZone = safeZones.get(safeZones.size() - 1);
      var deltaRadius = previousSafeZone.radius - radius;
      safeZones.add(
          new Circle(0, 0, radius)
          //              MathUtil.randomPosition(
          //                  previousSafeZone.getRight().getX() - deltaRadius,
          //                  previousSafeZone.getRight().getX() + deltaRadius,
          //                  previousSafeZone.getRight().getY() - deltaRadius,
          //                  previousSafeZone.getRight().getY() + deltaRadius)
          );
    }
    nextSafeZone = 1;
  }

  public void stop() {
    timer.cancel();
    //    EzyFoxUtil.getInstance().getMatchingService().destroyMatch(this.getId());
  }

  public boolean randomPositionForObstacle(Obstacle obstacle) {
    var newPosition = MatchUtil.randomPosition(100, 9900, 100, 9900);
    obstacle.setPosition(newPosition);
    for (var player : players.values()) {
      if (MatchUtil.isCollision(player.getShape(), obstacle.getShape())) {
        return false;
      }
    }
    for (var object : objects.values()) {
      if (object instanceof Obstacle otherObstacle) {
        if (MatchUtil.isCollision(otherObstacle.getShape(), obstacle.getShape())) {
          return false;
        }
      }
    }
    return true;
  }

  private void initObstacles() {
    // TODO: random this
    for (int i = 0; i < 500; i++) {
      var tree = new Tree();
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

    for (int i = 0; i < 500; i++) {

      var container = new Container();
      container.setShape(new Rectangle(0, 0, 100, 100));
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

          public void run() {
            update();
          }
        };
    timer.scheduleAtFixedRate(gameLoopTask, 0, GameConstant.PERIOD_PER_TICK);
  }

  private void sendMatchStart() {
    var builder = new FlatBufferBuilder(0);
    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.StartGameResponse);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    zoneContext.stream(bytes, getSessions(getAllPlayers()));
  }

  public void end() {
    gameLoopTask.cancel();
    timer.cancel();
  }

  public void update() {
    currentTick++;
    updateSafeZone();
    updatePlayers();
    updateMapObjects();
  }

  private void updateSafeZone() {
    if (currentTick % GameConfig.getInstance().getTicksPerSafeZone() != 0) return;
    nextSafeZone++;
    {
      var builder = new FlatBufferBuilder(0);

      SafeZoneMoveResponse.startSafeZoneMoveResponse(builder);
      var responseOffset = SafeZoneMoveResponse.endSafeZoneMoveResponse(builder);

      Response.startResponse(builder);
      Response.addResponseType(builder, ResponseUnion.SafeZoneMoveResponse);
      Response.addResponse(builder, responseOffset);
      var packetOffset = Response.endResponse(builder);
      builder.finish(packetOffset);

      var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
      zoneContext.stream(bytes, getSessions(getAllPlayers()));
    }
    if (nextSafeZone >= safeZones.size()) {
      return;
    }
    var safeZone = safeZones.get(nextSafeZone);
    var builder = new FlatBufferBuilder(0);

    NewSafeZoneResponse.startNewSafeZoneResponse(builder);
    var safeZoneOffset = Vector2Struct.createVector2Struct(builder, safeZone.x, safeZone.y);
    NewSafeZoneResponse.addSafeZone(builder, safeZoneOffset);
    var responseOffset = NewSafeZoneResponse.endNewSafeZoneResponse(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.NewSafeZoneResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    zoneContext.stream(bytes, getSessions(getAllPlayers()));
  }

  private void updateMapObjects() {
    for (var mapObject : objects.values()) {
      if (mapObject instanceof Bullet bullet) {
        bullet.move();
        log.info("bullet position {}", bullet.getPosition());
        String ownerId = bullet.getOwnerId();
        var owner = players.get(ownerId);
        for (var player : players.values()) {
          log.info("player's team {}, owner's team {}", player.getTeam(), owner.getTeam());
          log.info("player's hp {}, player is dead {}", player.getHp(), player.isDestroyed());
          if (player.getTeam() == owner.getTeam() || player.isDestroyed()) {
            continue;
          }
          log.info("player position {}", player.getPosition());
          if (MatchUtil.isCollision(player.getShape(), bullet.getShape())) {
            log.info("player {} is hit by bullet {}", player.getPlayerId(), bullet.getId());
            makeDamage(
                ownerId,
                new Circle(
                    bullet.getPosition().x,
                    bullet.getPosition().y,
                    bullet.getType().getDamageRadius()),
                bullet.getType().getDamage());
            bullet.setDestroyed(true);
          }
        }
        for (var otherObject : objects.values()) {
          if (otherObject == mapObject) {
            continue; // Chính nó
          }
          if (otherObject instanceof Destroyable destroyable) {
            if (destroyable.isDestroyed()) {
              continue;
            }
          }
          if (MatchUtil.isCollision(otherObject.getShape(), bullet.getShape())) {
            log.info("object {} is hit by bullet {}", otherObject.getId(), bullet.getId());
            makeDamage(
                ownerId,
                new Circle(
                    bullet.getPosition().x,
                    bullet.getPosition().y,
                    bullet.getType().getDamageRadius()),
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
    for (var player : players.values()) {
      var playerActionMap = playerRequests.get(player.getPlayerId());
      if (playerActionMap == null) {
        continue;
      }
      for (var action : playerActionMap.values()) {
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
    if (action instanceof PlayerMove playerMove) {
      onPlayerMove(playerId, playerMove.getDirection(), playerMove.getRotation());
    } else if (action instanceof PlayerAttack) {
      var player = players.get(playerId);
      onPlayerAttack(playerId, player.getAttackDirection());
    } else if (action instanceof PlayerChangeWeapon playerChangeWeapon) {
      onPlayerSwitchWeapon(playerId, playerChangeWeapon.getWeaponIndex());
    } else if (action instanceof PlayerReloadWeapon) {
      onPlayerReloadWeapon(playerId);
    } else if (action instanceof PlayerTakeItem) {
      onPlayerTakeItem(playerId);
    }
  }

  private void createItemOnMap(Item item, Vector2 position, Vector2 rawPosition) {
    var randomNeighborPosition =
        new Vector2(ThreadLocalRandom.current().nextFloat(0, 20) - 10, ThreadLocalRandom.current().nextFloat(0, 20) - 10);
    var itemOnMap =
        ItemOnMap.builder().item(item).position(position.add(randomNeighborPosition)).build();
    addMapObject(itemOnMap);
    var builder = new FlatBufferBuilder(0);
    var itemOffset = 0;
    byte itemType = 0;
    if (item instanceof BulletItem bulletItem) {
      itemType = ItemUnion.BulletItemTable;
      itemOffset =
          BulletItemTable.createBulletItemTable(builder, (byte) bulletItem.getBulletType().ordinal());
    } else if (item instanceof GunItem gunItem) {
      itemType = ItemUnion.GunItemTable;
      itemOffset = GunItemTable.createGunItemTable(builder, (byte) gunItem.getGunType().ordinal());
    }
    var positionOffset = Vector2Struct.createVector2Struct(builder, rawPosition.x, rawPosition.y);
    var rawPositionOffset = Vector2Struct.createVector2Struct(builder, rawPosition.x, rawPosition.y);

    CreateItemOnMapResponse.startCreateItemOnMapResponse(builder);
    CreateItemOnMapResponse.addItem(builder, itemOffset);
    CreateItemOnMapResponse.addItemType(builder, itemType);
    CreateItemOnMapResponse.addPosition(builder, positionOffset);
    CreateItemOnMapResponse.addRawPosition(builder, rawPositionOffset);
    var responseOffset = CreateItemOnMapResponse.endCreateItemOnMapResponse(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.CreateItemOnMapResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    zoneContext.stream(bytes, getSessions(getAllPlayers()));
  }

  private void onPlayerTakeItem(String playerId) {
    var player = players.get(playerId);
    for (var object : objects.values()) {
      if (!(object instanceof ItemOnMap itemOnMap)) {
        continue;
      }
      if (MatchUtil.isCollision(player.getShape(), itemOnMap.getShape())) {
        //        player.addItem(itemOnMap.getItem()); //TODO: add item to player
        objects.remove(itemOnMap.getId());

        var builder = new FlatBufferBuilder(0);
        var usernameOffset = builder.createString(playerId);

        var responseOffset =
            PlayerTakeItemResponse.createPlayerTakeItemResponse(
                builder, usernameOffset, object.getId());

        Response.startResponse(builder);
        Response.addResponseType(builder, ResponseUnion.PlayerTakeItemResponse);
        Response.addResponse(builder, responseOffset);
        var packetOffset = Response.endResponse(builder);
        builder.finish(packetOffset);

        var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
        zoneContext.stream(bytes, getSessions(getAllPlayers()));
      }
    }
  }

  private void onPlayerReloadWeapon(String playerId) {
    var player = players.get(playerId);
    player.reloadWeapon();
    var builder = new FlatBufferBuilder(0);

    var responseOffset =
        PlayerReloadWeaponResponse.createPlayerReloadWeaponResponse(builder, 1000, 100); // FIXME

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.PlayerDeadResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    zoneContext.stream(bytes, getSessions(getAllPlayers()));
  }
}
