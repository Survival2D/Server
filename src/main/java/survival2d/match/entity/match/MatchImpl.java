package survival2d.match.entity.match;

import static survival2d.match.constant.GameConstant.DAMAGE_SHAPE;
import static survival2d.match.constant.GameConstant.DOUBLE_MAX_OBJECT_SIZE;
import static survival2d.match.constant.GameConstant.QUAD_MAX_OBJECT_SIZE;

import com.google.flatbuffers.FlatBufferBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.common.CommonConfig;
import survival2d.flatbuffers.MapObjectData;
import survival2d.flatbuffers.MatchInfoResponse;
import survival2d.flatbuffers.Packet;
import survival2d.flatbuffers.PacketData;
import survival2d.flatbuffers.Vec2;
import survival2d.match.action.PlayerAction;
import survival2d.match.action.PlayerAttack;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerMove;
import survival2d.match.action.PlayerReloadWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.match.config.GameConfig;
import survival2d.match.constant.GameConstant;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.Containable;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Shape;
import survival2d.match.entity.config.AttachType;
import survival2d.match.entity.config.BulletType;
import survival2d.match.entity.config.GunType;
import survival2d.match.entity.config.HelmetType;
import survival2d.match.entity.config.VestType;
import survival2d.match.entity.item.BackPackItem;
import survival2d.match.entity.item.BandageItem;
import survival2d.match.entity.item.BulletItem;
import survival2d.match.entity.item.GunItem;
import survival2d.match.entity.item.HelmetItem;
import survival2d.match.entity.item.ItemOnMap;
import survival2d.match.entity.item.MedKitItem;
import survival2d.match.entity.item.VestItem;
import survival2d.match.entity.obstacle.Container;
import survival2d.match.entity.obstacle.Obstacle;
import survival2d.match.entity.obstacle.Stone;
import survival2d.match.entity.obstacle.Tree;
import survival2d.match.entity.obstacle.Wall;
import survival2d.match.entity.player.Player;
import survival2d.match.entity.player.PlayerImpl;
import survival2d.match.entity.quadtree.QuadTree;
import survival2d.match.entity.quadtree.RectangleBoundary;
import survival2d.match.entity.quadtree.SpatialPartitionGeneric;
import survival2d.match.entity.weapon.Bullet;
import survival2d.match.util.MapGenerator;
import survival2d.util.ezyfox.EzyFoxUtil;
import survival2d.util.math.MathUtil;
import survival2d.util.serialize.ExcludeFromGson;
import survival2d.util.stream.ByteBufferUtil;

@Getter
@Slf4j
public class MatchImpl extends SpatialPartitionGeneric<MapObject> implements Match {
  @ExcludeFromGson private final int id;

  @ExcludeFromGson
  private final Map<String, Map<Class<? extends PlayerAction>, PlayerAction>> playerRequests =
      new ConcurrentHashMap<>();

  private final Map<String, Player> players = new ConcurrentHashMap<>();
  @ExcludeFromGson private final Timer timer = new Timer();
  @ExcludeFromGson private final List<Pair<Circle, Vector2D>> safeZones = new ArrayList<>();
  @ExcludeFromGson int nextSafeZone;
  @ExcludeFromGson private int currentMapObjectId;
  @ExcludeFromGson private TimerTask gameLoopTask;
  @ExcludeFromGson private long currentTick;

  public MatchImpl(int id) {
    this.id = id;
    objects = new Hashtable<>();
    quadTree =
        new QuadTree<>(
            0, 0, GameConfig.getInstance().getMapWidth(), GameConfig.getInstance().getMapHeight());
    init();
  }

  @Override
  public void addPlayer(int teamId, String playerId) {
    PlayerImpl value = new PlayerImpl(playerId, teamId);
    players.putIfAbsent(playerId, value);
    int tryCount = 0;
    while (!randomPositionForPlayer(playerId)) {
      tryCount++;
      if (tryCount > 100) {
        log.error("Can't find position for player {}", playerId);
        break;
      }
    }
    playerRequests.put(playerId, new ConcurrentHashMap<>());
    addMapObject(players.get(playerId));
  }

  public boolean randomPositionForPlayer(String playerId) {
    val player = players.get(playerId);
    val newPosition =
        new Vector2D(RandomUtils.nextDouble(100, 900), RandomUtils.nextDouble(100, 900));
    player.setPosition(newPosition);
    for (val object : getNearBy(newPosition)) {
      if (object instanceof Obstacle) {
        val obstacle = (Obstacle) object;
        if (MathUtil.isIntersect(
            player.getPosition(), player.getShape(), obstacle.getPosition(), obstacle.getShape())) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public Collection<String> getAllUsernames() {
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
    if (!MathUtil.isZero(direction)) {
      val unitDirection = direction.normalize();
      val moveBy = unitDirection.scalarMultiply(player.getSpeed());
      player.moveBy(moveBy);
      if (!isValidToMove(player)) {
        log.warn("Player {} can not move to {}", playerId, player.getPosition());
        val reverse = moveBy.scalarMultiply(-1);
        player.moveBy(reverse);
      }
    }
    player.setRotation(rotation);
    onMapObjectMove(player);

    val builder = new FlatBufferBuilder(0);
    val usernameOffset = builder.createString(playerId);

    survival2d.flatbuffers.PlayerMoveResponse.startPlayerMoveResponse(builder);
    survival2d.flatbuffers.PlayerMoveResponse.addUsername(builder, usernameOffset);
    survival2d.flatbuffers.PlayerMoveResponse.addRotation(builder, rotation);
    val positionOffset =
        Vec2.createVec2(builder, player.getPosition().getX(), player.getPosition().getY());
    survival2d.flatbuffers.PlayerMoveResponse.addPosition(builder, positionOffset);
    val responseOffset = survival2d.flatbuffers.PlayerMoveResponse.endPlayerMoveResponse(builder);

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.PlayerMoveResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    EzyFoxUtil.stream(bytes, getAllUsernames());
  }

  private Collection<MapObject> getNearBy(Vector2D position) {
    return quadTree.query(
        new RectangleBoundary(
            position.getX() - DOUBLE_MAX_OBJECT_SIZE,
            position.getY() - DOUBLE_MAX_OBJECT_SIZE,
            QUAD_MAX_OBJECT_SIZE,
            QUAD_MAX_OBJECT_SIZE));
  }

  private Collection<Obstacle> getNearByObstacle(Vector2D position) {
    return getNearBy(position).stream()
        .filter(object -> object instanceof Obstacle)
        .map(object -> (Obstacle) object)
        .collect(Collectors.toList());
  }

  private Collection<Player> getNearByPlayer(Vector2D position) {
    return getNearBy(position).stream()
        .filter(object -> object instanceof Player)
        .map(object -> (Player) object)
        .collect(Collectors.toList());
  }

  private boolean isValidPositionForPlayer(Vector2D position) {
    return position.getX() - PlayerImpl.BODY_RADIUS >= 0
        && position.getX() + PlayerImpl.BODY_RADIUS <= GameConfig.getInstance().getMapWidth()
        && position.getY() - PlayerImpl.BODY_RADIUS >= 0
        && position.getY() + PlayerImpl.BODY_RADIUS <= GameConfig.getInstance().getMapHeight();
  }

  private boolean isValidToMove(MapObject mapObject) {
    val isCollide = isCollisionWithObstacle(mapObject);
    if (isCollide) {
      return false;
    }
    return isValidPositionForPlayer(mapObject.getPosition());
  }

  private boolean isCollisionWithObstacle(MapObject mapObject) {
    return getNearBy(mapObject.getPosition()).stream()
        .filter(object -> object instanceof Obstacle)
        .anyMatch(
            object -> {
              val obstacle = (Obstacle) object;
              return MathUtil.isIntersect(
                  mapObject.getPosition(),
                  mapObject.getShape(),
                  obstacle.getPosition(),
                  obstacle.getShape());
            });
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
              .add(
                  player
                      .getAttackDirection()
                      .scalarMultiply(((Circle) player.getShape()).getRadius())),
          DAMAGE_SHAPE,
          5);
    } else if (currentWeapon.getAttachType() == AttachType.RANGE) {
      createBullet(
          playerId,
          player
              .getPosition()
              .add(
                  player
                      .getAttackDirection()
                      .scalarMultiply(
                          ((Circle) player.getShape()).getRadius()
                              + GameConstant.INITIAL_BULLET_DISTANCE)),
          direction,
          BulletType.NORMAL);
    }
  }

  public void addMapObject(MapObject object) {
    object.setId(currentMapObjectId++);
    objects.put(object.getId(), object);
    quadTree.add(object);
  }

  public void onMapObjectMove(MapObject object) {
    quadTree.update(object);
  }

  @Override
  public void createDamage(String playerId, Vector2D position, Shape shape, double damage) {
    val player = players.get(playerId);
    log.warn("match is not present");

    val builder = new FlatBufferBuilder(0);
    val usernameOffset = builder.createString(playerId);

    survival2d.flatbuffers.PlayerAttackResponse.startPlayerAttackResponse(builder);
    survival2d.flatbuffers.PlayerAttackResponse.addUsername(builder, usernameOffset);
    val positionOffset = Vec2.createVec2(builder, position.getX(), position.getY());
    survival2d.flatbuffers.PlayerMoveResponse.addPosition(builder, positionOffset);
    val responseOffset =
        survival2d.flatbuffers.PlayerAttackResponse.endPlayerAttackResponse(builder);

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.PlayerAttackResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    EzyFoxUtil.stream(bytes, getAllUsernames());

    makeDamage(playerId, position, shape, damage);
  }

  @Override
  public void makeDamage(String playerId, Vector2D position, Shape shape, double damage) {
    val currentPlayer = players.get(playerId);
    for (val object : getNearBy(position)) {
      if (object instanceof Player) {
        val player = (Player) object;
        // Cùng team thì không tính damage
        if (player.getTeam() == currentPlayer.getTeam()) continue;
        // Chính người chơi đó thì mới không tính damage
        // if (Objects.equals(player.getPlayerId(), playerId)) continue;
        if (player.isDestroyed()) continue;
        if (MathUtil.isIntersect(player.getPosition(), player.getShape(), position, shape)) {
          val isHeadshot =
              MathUtil.isIntersect(player.getPosition(), player.getHead(), position, shape);
          val damageMultiple = isHeadshot ? GameConstant.HEADSHOT_DAMAGE : GameConstant.BODY_DAMAGE;
          val totalDamage = damage * damageMultiple;
          val reduceDamage =
              isHeadshot
                  ? player.getHelmetType().getReduceDamage()
                  : player.getVestType().getReduceDamage();
          val finalDamage = totalDamage - reduceDamage;
          player.reduceHp(finalDamage);
          {
            val builder = new FlatBufferBuilder(0);
            val usernameOffset = builder.createString(player.getPlayerId());

            survival2d.flatbuffers.PlayerTakeDamageResponse.startPlayerTakeDamageResponse(builder);
            survival2d.flatbuffers.PlayerTakeDamageResponse.addUsername(builder, usernameOffset);
            survival2d.flatbuffers.PlayerTakeDamageResponse.addRemainHp(builder, player.getHp());
            val responseOffset =
                survival2d.flatbuffers.PlayerTakeDamageResponse.endPlayerTakeDamageResponse(
                    builder);

            Packet.startPacket(builder);
            Packet.addDataType(builder, PacketData.PlayerTakeDamageResponse);
            Packet.addData(builder, responseOffset);
            val packetOffset = Packet.endPacket(builder);
            builder.finish(packetOffset);

            val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
            EzyFoxUtil.stream(bytes, getAllUsernames());
          }
          if (player.isDestroyed()) {
            val builder = new FlatBufferBuilder(0);
            val usernameOffset = builder.createString(playerId);

            survival2d.flatbuffers.PlayerDeadResponse.startPlayerDeadResponse(builder);
            survival2d.flatbuffers.PlayerDeadResponse.addUsername(builder, usernameOffset);
            val responseOffset =
                survival2d.flatbuffers.PlayerDeadResponse.endPlayerDeadResponse(builder);

            Packet.startPacket(builder);
            Packet.addDataType(builder, PacketData.PlayerDeadResponse);
            Packet.addData(builder, responseOffset);
            val packetOffset = Packet.endPacket(builder);
            builder.finish(packetOffset);

            val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
            EzyFoxUtil.stream(bytes, getAllUsernames());
            checkEndGame();
          }
        }
      } else if (object instanceof Obstacle) {
        val obstacle = (Obstacle) object;
        if (!(obstacle instanceof Destroyable)) {
          continue;
        }
        val destroyable = (Destroyable) obstacle;
        if (destroyable.isDestroyed()) {
          continue;
        }
        val hasHp = (HasHp) obstacle;
        if (MathUtil.isIntersect(obstacle.getPosition(), obstacle.getShape(), position, shape)) {
          hasHp.reduceHp(damage);
          log.info(
              "Obstacle {} take damage {}, remainHp {}", obstacle.getId(), damage, hasHp.getHp());
          {
            val builder = new FlatBufferBuilder(0);

            val responseOffset =
                survival2d.flatbuffers.ObstacleTakeDamageResponse.createObstacleTakeDamageResponse(
                    builder, obstacle.getId(), hasHp.getHp());

            Packet.startPacket(builder);
            Packet.addDataType(builder, PacketData.ObstacleTakeDamageResponse);
            Packet.addData(builder, responseOffset);
            val packetOffset = Packet.endPacket(builder);
            builder.finish(packetOffset);

            val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
            EzyFoxUtil.stream(bytes, getAllUsernames());
          }
          if (destroyable.isDestroyed()) {
            log.info("Obstacle {} destroyed", obstacle.getId());
            val builder = new FlatBufferBuilder(0);

            val responseOffset =
                survival2d.flatbuffers.ObstacleDestroyResponse.createObstacleDestroyResponse(
                    builder, obstacle.getId());

            Packet.startPacket(builder);
            Packet.addDataType(builder, PacketData.ObstacleDestroyResponse);
            Packet.addData(builder, responseOffset);
            val packetOffset = Packet.endPacket(builder);
            builder.finish(packetOffset);

            val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
            EzyFoxUtil.stream(bytes, getAllUsernames());
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
      val builder = new FlatBufferBuilder(0);

      val responseOffset =
          survival2d.flatbuffers.EndGameResponse.createEndGameResponse(builder, winTeam);

      Packet.startPacket(builder);
      Packet.addDataType(builder, PacketData.EndGameResponse);
      Packet.addData(builder, responseOffset);
      val packetOffset = Packet.endPacket(builder);
      builder.finish(packetOffset);

      val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
      EzyFoxUtil.stream(bytes, getAllUsernames());
      stop();
    }
  }

  @Override
  public void createBullet(
      String playerId, Vector2D position, Vector2D direction, BulletType type) {
    val bullet = new Bullet(playerId, position, direction, type);
    addMapObject(bullet);

    val builder = new FlatBufferBuilder(0);
    val usernameOffset = builder.createString(playerId);

    survival2d.flatbuffers.Bullet.startBullet(builder);
    survival2d.flatbuffers.Bullet.addType(builder, (byte) type.ordinal());
    survival2d.flatbuffers.Bullet.addId(builder, bullet.getId());
    val positionOffset =
        survival2d.flatbuffers.Vec2.createVec2(builder, position.getX(), position.getY());
    survival2d.flatbuffers.Bullet.addPosition(builder, positionOffset);
    val directionOffset =
        survival2d.flatbuffers.Vec2.createVec2(builder, direction.getX(), direction.getY());
    survival2d.flatbuffers.Bullet.addDirection(builder, directionOffset);
    survival2d.flatbuffers.Bullet.addOwner(builder, usernameOffset);
    val bulletOffset = survival2d.flatbuffers.Bullet.endBullet(builder);
    val responseOffset =
        survival2d.flatbuffers.CreateBulletOnMapResponse.createCreateBulletOnMapResponse(
            builder, bulletOffset);

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.CreateBulletOnMapResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    EzyFoxUtil.stream(bytes, getAllUsernames());
  }

  @Override
  public void responseMatchInfo(String username) {
    final byte[] bytes = getMatchInfoPacket();
    EzyFoxUtil.stream(bytes, username);
  }

  private byte[] getMatchInfoPacket() {
    val builder = new FlatBufferBuilder(0);

    final int responseOffset = putMatchInfoData(builder);

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.MatchInfoResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    return ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
  }

  public int putMatchInfoData(FlatBufferBuilder builder) {
    int[] playerOffsets = new int[players.size()];
    val players = this.players.values().toArray(new Player[0]);
    for (int i = 0; i < players.length; i++) {
      val player = players[i];
      val usernameOffset = builder.createString(player.getPlayerId());
      survival2d.flatbuffers.Player.startPlayer(builder);
      survival2d.flatbuffers.Player.addUsername(builder, usernameOffset);
      val positionOffset =
          Vec2.createVec2(builder, player.getPosition().getX(), player.getPosition().getY());
      survival2d.flatbuffers.Player.addPosition(builder, positionOffset);
      survival2d.flatbuffers.Player.addRotation(builder, player.getRotation());
      survival2d.flatbuffers.Player.addTeam(builder, player.getTeam());
      playerOffsets[i] = survival2d.flatbuffers.Player.endPlayer(builder);
    }

    int[] mapObjectOffsets = new int[objects.size()];
    val mapObjects = objects.values().toArray(new MapObject[0]);
    for (int i = 0; i < mapObjects.length; i++) {
      val object = mapObjects[i];
      var objectDataOffset = 0;
      byte objectDataType = 0;
      if (object instanceof BulletItem) {
        objectDataType = MapObjectData.BulletItem;
        val bulletItem = (BulletItem) object;
        survival2d.flatbuffers.BulletItem.startBulletItem(builder);
        survival2d.flatbuffers.BulletItem.addType(
            builder, (byte) bulletItem.getBulletType().ordinal());
        val bulletItemOffset = survival2d.flatbuffers.BulletItem.endBulletItem(builder);
        survival2d.flatbuffers.MapObject.addData(builder, bulletItemOffset);
      } else if (object instanceof GunItem) {
        objectDataType = MapObjectData.GunItem;
        val gunItem = (GunItem) object;
        survival2d.flatbuffers.GunItem.startGunItem(builder);
        survival2d.flatbuffers.GunItem.addType(builder, (byte) gunItem.getGunType().ordinal());
        val gunItemOffset = survival2d.flatbuffers.GunItem.endGunItem(builder);
        survival2d.flatbuffers.MapObject.addData(builder, gunItemOffset);
      } else if (object instanceof Tree) {
        objectDataType = MapObjectData.Tree;
        val tree = (Tree) object;
        survival2d.flatbuffers.Tree.startTree(builder);
        val treeOffset = survival2d.flatbuffers.Tree.endTree(builder);
        survival2d.flatbuffers.MapObject.addData(builder, treeOffset);
      } else if (object instanceof Container) {
        objectDataType = MapObjectData.Container;
        val container = (Container) object;
        survival2d.flatbuffers.Container.startContainer(builder);
        val containerOffset = survival2d.flatbuffers.Container.endContainer(builder);
        survival2d.flatbuffers.MapObject.addData(builder, containerOffset);
      } else if (object instanceof Stone) {
        objectDataType = MapObjectData.Stone;
        val stone = (Stone) object;
        survival2d.flatbuffers.Stone.startStone(builder);
        val stoneOffset = survival2d.flatbuffers.Stone.endStone(builder);
        survival2d.flatbuffers.MapObject.addData(builder, stoneOffset);
      } else if (object instanceof Wall) {
        objectDataType = MapObjectData.Wall;
        val wall = (Wall) object;
        survival2d.flatbuffers.Wall.startWall(builder);
        val wallOffset = survival2d.flatbuffers.Wall.endWall(builder);
        survival2d.flatbuffers.MapObject.addData(builder, wallOffset);
      }
      survival2d.flatbuffers.MapObject.startMapObject(builder);
      survival2d.flatbuffers.MapObject.addId(builder, object.getId());
      val positionOffset =
          Vec2.createVec2(builder, object.getPosition().getX(), object.getPosition().getY());
      survival2d.flatbuffers.MapObject.addPosition(builder, positionOffset);
      survival2d.flatbuffers.MapObject.addDataType(builder, objectDataType);
      survival2d.flatbuffers.MapObject.addData(builder, objectDataOffset);
      mapObjectOffsets[i] = survival2d.flatbuffers.MapObject.endMapObject(builder);
    }

    val playersOffset = MatchInfoResponse.createPlayersVector(builder, playerOffsets);
    val mapObjectsOffset = MatchInfoResponse.createMapObjectsVector(builder, mapObjectOffsets);

    MatchInfoResponse.startMatchInfoResponse(builder);
    MatchInfoResponse.addPlayers(builder, playersOffset);
    MatchInfoResponse.addMapObjects(builder, mapObjectsOffset);
    val safeZoneOffset =
        Vec2.createVec2(
            builder, safeZones.get(0).getRight().getX(), safeZones.get(0).getRight().getY());
    MatchInfoResponse.addSafeZone(builder, safeZoneOffset);
    return MatchInfoResponse.endMatchInfoResponse(builder);
  }

  @Override
  public void responseMatchInfo() {
    val bytes = getMatchInfoPacket();
    EzyFoxUtil.stream(bytes, getAllUsernames());
  }

  public void onPlayerSwitchWeapon(String playerId, int weaponId) {
    val player = players.get(playerId);
    if (player == null) {
      log.error("player {} is null", playerId);
      return;
    }
    player.switchWeapon(weaponId);
    val builder = new FlatBufferBuilder(0);
    val usernameOffset = builder.createString(playerId);

    val responseOffset =
        survival2d.flatbuffers.PlayerChangeWeaponResponse.createPlayerChangeWeaponResponse(
            builder, usernameOffset, (byte) weaponId);

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.PlayerChangeWeaponResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    EzyFoxUtil.stream(bytes, getAllUsernames());
  }

  public void init() {
    if (!CommonConfig.testPing) {
      timer.schedule(
          new TimerTask() {
            @Override
            public void run() {
              start();
            }
          },
          3000);
    }
    initSafeZones();
    initObstacles();
  }

  private void initSafeZones() {
    safeZones.add(
        new ImmutablePair<>(
            new Circle(GameConfig.getInstance().getDefaultSafeZoneRadius()),
            new Vector2D(
                GameConfig.getInstance().getDefaultSafeZoneCenterX(),
                GameConfig.getInstance().getDefaultSafeZoneCenterY())));
    for (val radius : GameConfig.getInstance().getSafeZonesRadius()) {
      val previousSafeZone = safeZones.get(safeZones.size() - 1);
      val deltaRadius = previousSafeZone.getLeft().getRadius() - radius;
      val newPosition =
          CommonConfig.testPing
              ? new Vector2D(0, 0)
              : MathUtil.randomPosition(
                  previousSafeZone.getRight().getX() - deltaRadius,
                  previousSafeZone.getRight().getX() + deltaRadius,
                  previousSafeZone.getRight().getY() - deltaRadius,
                  previousSafeZone.getRight().getY() + deltaRadius);
      safeZones.add(new ImmutablePair<>(new Circle(radius), newPosition));
    }
    nextSafeZone = 1;
  }

  public void stop() {
    gameLoopTask.cancel();
    timer.cancel();
    EzyFoxUtil.getMatchingService().destroyMatch(id);
  }

  private void initObstacles() {
    val generateResult = MapGenerator.generateMap();
    for (val obstacle : generateResult.getMapObjects()) {
      val position =
          new Vector2D(obstacle.getPosition().getX() * 100, obstacle.getPosition().getY() * 100);
      switch (obstacle.getType()) {
        case WALL:
          {
            val wall = new Wall();
            wall.setPosition(position);
            addMapObject(wall);
            break;
          }
        case TREE:
          {
            val tree = new Tree();
            // TODO: căn chỉnh lại vị trí cây do hình tròn
            tree.setPosition(position);
            addMapObject(tree);
            break;
          }
        case BOX:
          {
            val container = new Container();
            container.setPosition(position);
            // TODO: random items
            container.setItems(
                Arrays.asList(
                    new GunItem(GunType.NORMAL, 10),
                    new BulletItem(BulletType.NORMAL, 10),
                    new VestItem(VestType.LEVEL_1),
                    new HelmetItem(HelmetType.LEVEL_1),
                    new BackPackItem(),
                    new MedKitItem(),
                    new BandageItem()));
            addMapObject(container);
            break;
          }
        case ROCK:
          {
            val stone = new Stone();
            stone.setPosition(position);
            addMapObject(stone);
            break;
          }
      }
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
    val builder = new FlatBufferBuilder(0);
    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.StartGameResponse);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    EzyFoxUtil.stream(bytes, getAllUsernames());
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
      val builder = new FlatBufferBuilder(0);

      survival2d.flatbuffers.SafeZoneMove.startSafeZoneMove(builder);
      val responseOffset = survival2d.flatbuffers.SafeZoneMove.endSafeZoneMove(builder);

      Packet.startPacket(builder);
      Packet.addDataType(builder, PacketData.SafeZoneMoveResponse);
      Packet.addData(builder, responseOffset);
      val packetOffset = Packet.endPacket(builder);
      builder.finish(packetOffset);

      val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
      EzyFoxUtil.stream(bytes, getAllUsernames());
    }
    if (nextSafeZone >= safeZones.size()) {
      return;
    }
    val safeZone = safeZones.get(nextSafeZone);
    val builder = new FlatBufferBuilder(0);

    survival2d.flatbuffers.NewSafeZoneResponse.startNewSafeZoneResponse(builder);
    val safeZoneOffset =
        survival2d.flatbuffers.Vec2.createVec2(
            builder, safeZone.getRight().getX(), safeZone.getRight().getY());
    survival2d.flatbuffers.NewSafeZoneResponse.addSafeZone(builder, safeZoneOffset);
    val responseOffset = survival2d.flatbuffers.NewSafeZoneResponse.endNewSafeZoneResponse(builder);

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.NewSafeZoneResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    EzyFoxUtil.stream(bytes, getAllUsernames());
  }

  private void updateMapObjects() {
    for (val mapObject : objects.values()) {
      if (mapObject instanceof Bullet) {
        val bullet = (Bullet) mapObject;
        bullet.move();
        onMapObjectMove(bullet);

        log.info("bullet position {}", bullet.getPosition());
        String ownerId = bullet.getOwnerId();
        val owner = players.get(ownerId);
        for (val object : getNearBy(bullet.getPosition())) {
          if (object instanceof Player) {
            val player = (Player) object;
            log.info("player's team {}, owner's team {}", player.getTeam(), owner.getTeam());
            log.info("player's hp {}, player is dead {}", player.getHp(), player.isDestroyed());
            if (player.getTeam() == owner.getTeam() || player.isDestroyed()) {
              continue;
            }
            log.info("player position {}", player.getPosition());
            if (MathUtil.isIntersect(
                player.getPosition(), player.getShape(), bullet.getPosition(), bullet.getShape())) {
              log.info("player {} is hit by bullet {}", player.getPlayerId(), bullet.getId());
              makeDamage(
                  ownerId,
                  bullet.getPosition(),
                  new Circle(bullet.getType().getDamageRadius()),
                  bullet.getType().getDamage());
              bullet.setDestroyed(true);
            }
          } else {
            if (object == mapObject) {
              continue; // Chính nó
            }
            if (object instanceof Destroyable) {
              val destroyable = (Destroyable) object;
              if (destroyable.isDestroyed()) {
                continue;
              }
            }
            if (MathUtil.isIntersect(
                object.getPosition(), object.getShape(), bullet.getPosition(), bullet.getShape())) {
              log.info("object {} is hit by bullet {}", object.getId(), bullet.getId());
              makeDamage(
                  ownerId,
                  bullet.getPosition(),
                  new Circle(bullet.getType().getDamageRadius()),
                  bullet.getType().getDamageRadius());
              bullet.setDestroyed(true);
            }
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
      onPlayerAttack(playerId, player.getAttackDirection());
    } else if (action instanceof PlayerChangeWeapon) {
      val playerChangeWeapon = (PlayerChangeWeapon) action;
      onPlayerSwitchWeapon(playerId, playerChangeWeapon.getWeaponIndex());
    } else if (action instanceof PlayerReloadWeapon) {
      onPlayerReloadWeapon(playerId);
    } else if (action instanceof PlayerTakeItem) {
      onPlayerTakeItem(playerId);
    }
  }

  private void createItemOnMap(Item item, Vector2D rawPosition) {
    val randomNeighborPosition = MathUtil.randomPosition(-100, 100, -100, 100);
    val position = rawPosition.add(randomNeighborPosition);
    val itemOnMap = ItemOnMap.builder().item(item).position(position).build();
    addMapObject(itemOnMap);
    val builder = new FlatBufferBuilder(0);
    var itemOffset = 0;
    byte itemType = 0;
    if (item instanceof BulletItem) {
      itemType = survival2d.flatbuffers.Item.BulletItem;
      val bulletItem = (BulletItem) item;
      itemOffset =
          survival2d.flatbuffers.BulletItem.createBulletItem(
              builder, (byte) bulletItem.getBulletType().ordinal(), bulletItem.getNumBullet());
    } else if (item instanceof GunItem) {
      itemType = survival2d.flatbuffers.Item.GunItem;
      val gunItem = (GunItem) item;
      itemOffset =
          survival2d.flatbuffers.GunItem.createGunItem(
              builder, (byte) gunItem.getGunType().ordinal(), gunItem.getNumBullet());
    } else if (item instanceof HelmetItem) {
      itemType = survival2d.flatbuffers.Item.HelmetItem;
      val helmetItem = (HelmetItem) item;
      itemOffset =
          survival2d.flatbuffers.HelmetItem.createHelmetItem(
              builder, (byte) helmetItem.getHelmetType().ordinal());
    } else if (item instanceof VestItem) {
      itemType = survival2d.flatbuffers.Item.VestItem;
      val vestItem = (VestItem) item;
      itemOffset =
          survival2d.flatbuffers.VestItem.createVestItem(
              builder, (byte) vestItem.getVestType().ordinal());
    } else if (item instanceof MedKitItem) {
      itemType = survival2d.flatbuffers.Item.MedKitItem;
      val medKitItem = (MedKitItem) item;
      survival2d.flatbuffers.MedKitItem.startMedKitItem(builder);
      itemOffset = survival2d.flatbuffers.MedKitItem.endMedKitItem(builder);
    } else if (item instanceof BandageItem) {
      itemType = survival2d.flatbuffers.Item.BandageItem;
      val bandageItem = (BandageItem) item;
      survival2d.flatbuffers.BandageItem.startBandageItem(builder);
      itemOffset = survival2d.flatbuffers.BandageItem.endBandageItem(builder);
    }

    survival2d.flatbuffers.CreateItemOnMapResponse.startCreateItemOnMapResponse(builder);
    survival2d.flatbuffers.CreateItemOnMapResponse.addId(builder, itemOnMap.getId());
    survival2d.flatbuffers.CreateItemOnMapResponse.addItem(builder, itemOffset);
    survival2d.flatbuffers.CreateItemOnMapResponse.addItemType(builder, itemType);
    val positionOffset =
        survival2d.flatbuffers.Vec2.createVec2(builder, position.getX(), position.getY());
    survival2d.flatbuffers.CreateItemOnMapResponse.addPosition(builder, positionOffset);
    val rawPositionOffset =
        survival2d.flatbuffers.Vec2.createVec2(builder, rawPosition.getX(), rawPosition.getY());
    survival2d.flatbuffers.CreateItemOnMapResponse.addRawPosition(builder, rawPositionOffset);
    val responseOffset =
        survival2d.flatbuffers.CreateItemOnMapResponse.endCreateItemOnMapResponse(builder);

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.CreateItemOnMapResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    EzyFoxUtil.stream(bytes, getAllUsernames());
  }

  private void onPlayerTakeItem(String playerId) {
    val player = players.get(playerId);
    for (val object : getNearBy(player.getPosition())) {
      if (!(object instanceof ItemOnMap)) {
        continue;
      }
      val itemOnMap = (ItemOnMap) object;
      if (MathUtil.isIntersect(
          player.getPosition(), player.getShape(), itemOnMap.getPosition(), itemOnMap.getShape())) {
        player.takeItem(itemOnMap.getItem());
        objects.remove(itemOnMap.getId());

        val builder = new FlatBufferBuilder(0);
        val usernameOffset = builder.createString(playerId);

        val responseOffset =
            survival2d.flatbuffers.PlayerTakeItemResponse.createPlayerTakeItemResponse(
                builder, usernameOffset, object.getId());

        Packet.startPacket(builder);
        Packet.addDataType(builder, PacketData.PlayerTakeItemResponse);
        Packet.addData(builder, responseOffset);
        val packetOffset = Packet.endPacket(builder);
        builder.finish(packetOffset);

        val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
        EzyFoxUtil.stream(bytes, getAllUsernames());
      }
    }
  }

  private void onPlayerReloadWeapon(String playerId) {
    val player = players.get(playerId);
    player.reloadWeapon();
    val builder = new FlatBufferBuilder(0);

    val responseOffset =
        survival2d.flatbuffers.PlayerReloadWeaponResponse.createPlayerReloadWeaponResponse(
            builder, 1000, 100); // FIXME

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.PlayerReloadWeaponResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    EzyFoxUtil.stream(bytes, getAllUsernames());
  }
}
