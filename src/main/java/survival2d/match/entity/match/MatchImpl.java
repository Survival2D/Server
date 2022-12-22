package survival2d.match.entity.match;

import static survival2d.match.constant.GameConstant.DAMAGE_SHAPE;
import static survival2d.match.constant.GameConstant.DOUBLE_MAX_OBJECT_SIZE;
import static survival2d.match.constant.GameConstant.QUAD_MAX_OBJECT_SIZE;

import com.google.flatbuffers.FlatBufferBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import survival2d.ai.bot.Bot;
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
import survival2d.match.action.PlayerUseHealItem;
import survival2d.match.config.GameConfig;
import survival2d.match.constant.GameConstant;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.Containable;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.Dot;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Shape;
import survival2d.match.entity.config.AttachType;
import survival2d.match.entity.config.BulletType;
import survival2d.match.entity.item.BackPackItem;
import survival2d.match.entity.item.BandageItem;
import survival2d.match.entity.item.BulletItem;
import survival2d.match.entity.item.GunItem;
import survival2d.match.entity.item.HelmetItem;
import survival2d.match.entity.item.ItemFactory;
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
import survival2d.match.entity.quadtree.BaseBoundary;
import survival2d.match.entity.quadtree.QuadTree;
import survival2d.match.entity.quadtree.RectangleBoundary;
import survival2d.match.entity.quadtree.SpatialPartitionGeneric;
import survival2d.match.entity.weapon.Bullet;
import survival2d.match.util.AStar;
import survival2d.match.util.AStar.Point;
import survival2d.match.util.MapGenerator;
import survival2d.match.util.TileObject;
import survival2d.util.ezyfox.EzyFoxUtil;
import survival2d.util.math.MathUtil;
import survival2d.util.serialize.ExcludeFromGson;
import survival2d.util.stream.ByteBufferUtil;
import survival2d.util.vision.VisionUtil;

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
  @ExcludeFromGson private final List<Vector2D> spawnPoints = new ArrayList<>();
  private final Map<String, Bot> bots = new ConcurrentHashMap<>();
  private final int NUM_BOTS = 1;
  @ExcludeFromGson int currentSafeZone;
  @ExcludeFromGson private int currentMapObjectId;
  @ExcludeFromGson private TimerTask gameLoopTask;
  @ExcludeFromGson private long currentTick;
  @ExcludeFromGson private AStar aStar;
  @ExcludeFromGson private int[][] grid;

  public MatchImpl(int id) {
    this.id = id;
    objects = new ConcurrentHashMap<>();
    quadTree =
        new QuadTree<>(
            0, 0, GameConfig.getInstance().getMapWidth(), GameConfig.getInstance().getMapHeight());
    init();
  }

  @Override
  public void addPlayer(int teamId, String playerId) {
    val player = new PlayerImpl(playerId, teamId);
    players.putIfAbsent(playerId, player);
    if (!spawnPoints.isEmpty()) {
      val spawnPoint = spawnPoints.remove(0);
      player.setPosition(spawnPoint);
    } else {
      int tryCount = 0;
      while (!randomPositionForPlayer(playerId)) {
        tryCount++;
        if (tryCount > 100) {
          log.error("Can't find position for player {}", playerId);
          break;
        }
      }
    }
    playerRequests.put(playerId, new ConcurrentHashMap<>());
    addMapObject(players.get(playerId));
  }

  public boolean randomPositionForPlayer(String playerId) {
    val player = players.get(playerId);
    val newPosition =
        new Vector2D(
            RandomUtils.nextDouble(
                PlayerImpl.BODY_RADIUS,
                GameConfig.getInstance().getMapWidth() - PlayerImpl.BODY_RADIUS),
            RandomUtils.nextDouble(
                PlayerImpl.BODY_RADIUS,
                GameConfig.getInstance().getMapHeight() - PlayerImpl.BODY_RADIUS));
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

  // Thường dùng cho obstacle, để gửi thông tin cho user có vùng nhìn ở đây
  public Collection<String> getUsernamesCanSeeAt(Vector2D position) {
    val result = new HashSet<String>();
    getNearByPlayer(position).forEach(p -> result.add(p.getPlayerId()));
    return result;
  }

  public Collection<String> getUsernamesCanSeeAtAndCheckUnderTree(Vector2D position) {
    val result = new HashSet<String>();
    val nearBy = getNearBy(position);
    val nearByTrees =
        nearBy.stream()
            .filter(o -> o instanceof Tree)
            .map(o -> (Tree) o)
            .filter(
                tree ->
                    MathUtil.isIntersect(position, Dot.DOT, tree.getPosition(), tree.getFoliage()))
            .collect(Collectors.toList());
    if (nearByTrees.isEmpty()) {
      getNearByPlayer(position).forEach(p -> result.add(p.getPlayerId()));
    } else {
      nearBy.stream()
          .filter(o -> o instanceof Player)
          .map(o -> (Player) o)
          .filter(
              player ->
                  nearByTrees.stream()
                      .allMatch(
                          tree ->
                              MathUtil.isIntersect(
                                  player.getPosition(),
                                  player.getShape(),
                                  tree.getPosition(),
                                  tree.getFoliage())))
          .map(Player::getPlayerId)
          .forEach(result::add);
    }
    return result;
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
    val oldPosition = player.getPosition();
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
    val newPosition = player.getPosition();
    val newMapObjects = new HashSet<MapObject>();
    if (!VisionUtil.isSameVisionX(oldPosition, newPosition)) {
      val boundary = VisionUtil.getBoundaryXAxis(oldPosition, newPosition);
      val query = quadTree.query(boundary);
      log.warn("BoundaryX {}", boundary);
      log.warn("QueryX {}", query);
      newMapObjects.addAll(query);
    }
    if (!VisionUtil.isSameVisionY(oldPosition, newPosition)) {
      val boundary = VisionUtil.getBoundaryYAxis(oldPosition, newPosition);
      val query = quadTree.query(boundary);
      log.warn("BoundaryY {}", boundary);
      log.warn("QueryY {}", query);
      newMapObjects.addAll(query);
    }
    if (!newMapObjects.isEmpty()) {
      log.warn(
          "Map objects {}",
          newMapObjects.stream()
              .map(object -> object.getClass().getSimpleName())
              .collect(Collectors.joining(",")));
      val data = getMatchInfoData(newMapObjects);
      EzyFoxUtil.stream(data, playerId);
    }

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
    EzyFoxUtil.stream(bytes, getUsernamesCanSeeAtAndCheckUnderTree(player.getPosition()));
  }

  private Collection<MapObject> getNearBy(Vector2D position) {
    return quadTree.query(
        new RectangleBoundary(
            position.getX() - DOUBLE_MAX_OBJECT_SIZE,
            position.getY() - DOUBLE_MAX_OBJECT_SIZE,
            QUAD_MAX_OBJECT_SIZE,
            QUAD_MAX_OBJECT_SIZE));
  }

  private Collection<MapObject> getNearByInVision(Vector2D position) {
    val width = GameConfig.getInstance().getPlayerViewWidth();
    val height = GameConfig.getInstance().getPlayerViewHeight();
    val boundary =
        new RectangleBoundary(
            position.getX() - width / 2, position.getY() - height / 2, width, height);
    return quadTree.query(boundary);
  }

  private Collection<Obstacle> getNearByObstacle(Vector2D position) {
    return getNearBy(position).stream()
        .filter(object -> object instanceof Obstacle)
        .map(object -> (Obstacle) object)
        .collect(Collectors.toList());
  }

  public Collection<Container> getNearByContainer(Vector2D position) {
    return getNearByInVision(position).stream()
        .filter(object -> object instanceof Container)
        .map(object -> (Container) object)
        .collect(Collectors.toList());
  }

  public Collection<ItemOnMap> getNearByItem(Vector2D position) {
    return getNearByInVision(position).stream()
        .filter(object -> object instanceof ItemOnMap)
        .map(object -> (ItemOnMap) object)
        .collect(Collectors.toList());
  }

  public Collection<Player> getNearByPlayer(Vector2D position) {
    return getNearByInVision(position).stream()
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
        .filter(
            object ->
                object instanceof Obstacle
                    && (!(object instanceof Destroyable) || !((Destroyable) object).isDestroyed()))
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
                      .scalarMultiply(PlayerImpl.BODY_RADIUS + 10)),
          DAMAGE_SHAPE,
          5);
    } else if (currentWeapon.getAttachType() == AttachType.RANGE) {
      if (!player.getGun().isReadyToShoot()) {
        return;
      }
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

  private void removeMapObject(MapObject mapObject) {
    objects.remove(mapObject.getId());
    quadTree.remove(mapObject);
    if (mapObject instanceof Obstacle) {
      // Xoá vật cản khỏi grid của A*
      val x = (int) (mapObject.getPosition().getX() / MapGenerator.TILE_SIZE);
      val y = (int) (mapObject.getPosition().getY() / MapGenerator.TILE_SIZE);
      if (mapObject instanceof Tree) {
        grid[x][y] = TileObject.EMPTY.ordinal();
      } else if (mapObject instanceof Container) {
        for (int i = x; i < x + TileObject.BOX.getWidth(); i++) {
          for (int j = y; j < y + TileObject.BOX.getHeight(); j++) {
            grid[i][j] = TileObject.EMPTY.ordinal();
          }
        }
      }
    }
  }

  public List<Vector2D> getPathFromTo(Vector2D from, Vector2D to) {
    val fromX = (int) (from.getX() / MapGenerator.TILE_SIZE);
    val fromY = (int) (from.getY() / MapGenerator.TILE_SIZE);
    val toX = (int) (to.getX() / MapGenerator.TILE_SIZE);
    val toY = (int) (to.getY() / MapGenerator.TILE_SIZE);
    return aStar.aStarSearch(new Point(fromX, fromY), new Point(toX, toY)).stream()
        .map(
            point -> {
              val x = point.getX() * MapGenerator.TILE_SIZE + MapGenerator.TILE_SIZE / 2;
              val y = point.getY() * MapGenerator.TILE_SIZE + MapGenerator.TILE_SIZE / 2;
              return new Vector2D(x, y);
            })
        .collect(Collectors.toList());
  }

  @Override
  public void setPlayerAutoPlay(String username, boolean enable) {
    val player = players.get(username);
    val bot =
        bots.computeIfAbsent(
            username,
            (k) -> {
              Bot b = new Bot();
              b.setMatch(this, username);
              b.setConfidencePercent(1.0);
              return b;
            });
    bot.setEnabled(enable);
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
    EzyFoxUtil.stream(bytes, getUsernamesCanSeeAt(position));

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
          onPlayerTakeDamage(player.getPlayerId(), finalDamage);
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
            EzyFoxUtil.stream(bytes, getUsernamesCanSeeAt(obstacle.getPosition()));
          }
          if (destroyable.isDestroyed()) {
            log.info("Obstacle {} destroyed", obstacle.getId());
            val builder = new FlatBufferBuilder(0);

            val responseOffset =
                survival2d.flatbuffers.ObstacleDestroyResponse.createObstacleDestroyResponse(
                    builder, obstacle.getId());
            removeMapObject(obstacle);

            Packet.startPacket(builder);
            Packet.addDataType(builder, PacketData.ObstacleDestroyResponse);
            Packet.addData(builder, responseOffset);
            val packetOffset = Packet.endPacket(builder);
            builder.finish(packetOffset);

            val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
            EzyFoxUtil.stream(bytes, getUsernamesCanSeeAt(obstacle.getPosition()));
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

  private void onPlayerTakeDamage(String playerId, double damage) {
    val player = players.get(playerId);
    player.reduceHp(damage);
    {
      val builder = new FlatBufferBuilder(0);
      val usernameOffset = builder.createString(player.getPlayerId());

      survival2d.flatbuffers.PlayerTakeDamageResponse.startPlayerTakeDamageResponse(builder);
      survival2d.flatbuffers.PlayerTakeDamageResponse.addUsername(builder, usernameOffset);
      survival2d.flatbuffers.PlayerTakeDamageResponse.addRemainHp(builder, player.getHp());
      val responseOffset =
          survival2d.flatbuffers.PlayerTakeDamageResponse.endPlayerTakeDamageResponse(builder);

      Packet.startPacket(builder);
      Packet.addDataType(builder, PacketData.PlayerTakeDamageResponse);
      Packet.addData(builder, responseOffset);
      val packetOffset = Packet.endPacket(builder);
      builder.finish(packetOffset);

      val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
      EzyFoxUtil.stream(bytes, getUsernamesCanSeeAt(player.getPosition()));
    }
    if (player.isDestroyed()) {
      val builder = new FlatBufferBuilder(0);
      val usernameOffset = builder.createString(player.getPlayerId());

      survival2d.flatbuffers.PlayerDeadResponse.startPlayerDeadResponse(builder);
      survival2d.flatbuffers.PlayerDeadResponse.addUsername(builder, usernameOffset);
      val responseOffset = survival2d.flatbuffers.PlayerDeadResponse.endPlayerDeadResponse(builder);

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
    //    final byte[] bytes = getMatchInfoData(objects.values());
    val player = players.get(username);
    val data = getMatchInfoData(objects.values());
    EzyFoxUtil.stream(data, username);
  }

  private byte[] getMatchInfoInBoundary(BaseBoundary boundary) {
    val objects = quadTree.query(boundary);
    return getMatchInfoData(objects);
  }

  private byte[] getMatchInfoData(Collection<MapObject> matchMapObjects) {
    val builder = new FlatBufferBuilder(0);
    builder.clear();

    final int responseOffset = putMatchInfoData(builder, matchMapObjects);

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.MatchInfoResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    return ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
  }

  public int putMatchInfoData(FlatBufferBuilder builder) {
    return putMatchInfoData(builder, objects.values());
  }

  public int putMatchInfoData(FlatBufferBuilder builder, Collection<MapObject> matchMapObjects) {
    val matchPlayers = // players.values();
        matchMapObjects.stream()
            .filter(o -> o instanceof Player)
            .map(player -> (Player) player)
            .collect(Collectors.toList());
    int[] playerOffsets = new int[matchPlayers.size()];
    val players = matchPlayers.toArray(new Player[0]);
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

    val mapObjects =
        matchMapObjects.stream()
            .filter(mapObject -> mapObject instanceof Obstacle || mapObject instanceof Item)
            .toArray(MapObject[]::new);
    int[] mapObjectOffsets = new int[mapObjects.length];
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
        objectDataOffset = survival2d.flatbuffers.BulletItem.endBulletItem(builder);
        //        val bulletItemOffset = survival2d.flatbuffers.BulletItem.endBulletItem(builder);
        //        survival2d.flatbuffers.MapObject.addData(builder, bulletItemOffset);
      } else if (object instanceof GunItem) {
        objectDataType = MapObjectData.GunItem;
        val gunItem = (GunItem) object;
        survival2d.flatbuffers.GunItem.startGunItem(builder);
        survival2d.flatbuffers.GunItem.addType(builder, (byte) gunItem.getGunType().ordinal());
        objectDataOffset = survival2d.flatbuffers.GunItem.endGunItem(builder);
        //        val gunItemOffset = survival2d.flatbuffers.GunItem.endGunItem(builder);
        //        survival2d.flatbuffers.MapObject.addData(builder, gunItemOffset);
      } else if (object instanceof HelmetItem) {
        objectDataType = MapObjectData.HelmetItem;
        val helmetItem = (HelmetItem) object;
        objectDataOffset =
            survival2d.flatbuffers.HelmetItem.createHelmetItem(
                builder, (byte) helmetItem.getHelmetType().ordinal());
      } else if (object instanceof VestItem) {
        objectDataType = MapObjectData.VestItem;
        val vestItem = (VestItem) object;
        objectDataOffset =
            survival2d.flatbuffers.VestItem.createVestItem(
                builder, (byte) vestItem.getVestType().ordinal());
      } else if (object instanceof MedKitItem) {
        objectDataType = MapObjectData.MedKitItem;
        val medKitItem = (MedKitItem) object;
        survival2d.flatbuffers.MedKitItem.startMedKitItem(builder);
        objectDataOffset = survival2d.flatbuffers.MedKitItem.endMedKitItem(builder);
      } else if (object instanceof BandageItem) {
        objectDataType = MapObjectData.BandageItem;
        val bandageItem = (BandageItem) object;
        survival2d.flatbuffers.BandageItem.startBandageItem(builder);
        objectDataOffset = survival2d.flatbuffers.BandageItem.endBandageItem(builder);
      } else if (object instanceof BackPackItem) {
        val backPackItem = (BackPackItem) object;
        objectDataType = MapObjectData.BackPackItem;
        survival2d.flatbuffers.BackPackItem.startBackPackItem(builder);
        objectDataOffset = survival2d.flatbuffers.BackPackItem.endBackPackItem(builder);
      } else if (object instanceof Tree) {
        objectDataType = MapObjectData.Tree;
        val tree = (Tree) object;
        survival2d.flatbuffers.Tree.startTree(builder);
        objectDataOffset = survival2d.flatbuffers.Tree.endTree(builder);
        //        val treeOffset = survival2d.flatbuffers.Tree.endTree(builder);
        //        survival2d.flatbuffers.MapObject.addData(builder, treeOffset);
      } else if (object instanceof Container) {
        objectDataType = MapObjectData.Container;
        val container = (Container) object;
        survival2d.flatbuffers.Container.startContainer(builder);
        objectDataOffset = survival2d.flatbuffers.Container.endContainer(builder);
        //        val containerOffset = survival2d.flatbuffers.Container.endContainer(builder);
        //        survival2d.flatbuffers.MapObject.addData(builder, containerOffset);
      } else if (object instanceof Stone) {
        objectDataType = MapObjectData.Stone;
        val stone = (Stone) object;
        survival2d.flatbuffers.Stone.startStone(builder);
        objectDataOffset = survival2d.flatbuffers.Stone.endStone(builder);
        //        val stoneOffset = survival2d.flatbuffers.Stone.endStone(builder);
        //        survival2d.flatbuffers.MapObject.addData(builder, stoneOffset);
      } else if (object instanceof Wall) {
        objectDataType = MapObjectData.Wall;
        val wall = (Wall) object;
        survival2d.flatbuffers.Wall.startWall(builder);
        objectDataOffset = survival2d.flatbuffers.Wall.endWall(builder);
        //        val wallOffset = survival2d.flatbuffers.Wall.endWall(builder);
        //        survival2d.flatbuffers.MapObject.addData(builder, wallOffset);
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
    //    val bytes = getMatchInfoData(objects.values());
    //    EzyFoxUtil.stream(bytes, getAllUsernames());
    for (val player : players.values()) {
      responseMatchInfo(player.getPlayerId());
    }
    sendNewSafeZoneInfo();
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
    //    initBots();
  }

  private void initBots() {
    for (int i = 0; i < NUM_BOTS; i++) {
      this.addPlayer(-1 - i, "bot_" + i);

      Bot bot = new Bot();
      bot.setMatch(this, "bot_" + i);
      bot.setConfidencePercent(1.0);
      bots.putIfAbsent("bot_" + i, bot);
    }
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
    currentSafeZone = -1;
  }

  public void stop() {
    gameLoopTask.cancel();
    timer.cancel();
    EzyFoxUtil.getMatchingService().destroyMatch(id);
  }

  private void initObstacles() {
    val generateResult = MapGenerator.generateMap();
    grid = generateResult.getTiles();
    aStar = new AStar(MapGenerator.MAP_WIDTH, MapGenerator.MAP_HEIGHT, grid);
    for (val tileObject : generateResult.getMapObjects()) {
      val position =
          new Vector2D(
              tileObject.getPosition().getX() * MapGenerator.TILE_SIZE,
              tileObject.getPosition().getY() * MapGenerator.TILE_SIZE);
      switch (tileObject.getType()) {
        case PLAYER:
          {
            spawnPoints.add(position.add(TileObject.PLAYER.getCenterOffset()));
            break;
          }
        case ITEM:
          {
            val item =
                ItemOnMap.builder().item(ItemFactory.randomItem()).position(position).build();
            addMapObject(item);
            break;
          }
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
            tree.setPosition(position.add(TileObject.TREE.getCenterOffset()));
            addMapObject(tree);
            break;
          }
        case BOX:
          {
            val container = new Container();
            container.setPosition(position);
            container.setItems(ItemFactory.randomItems());
            addMapObject(container);
            break;
          }
        case ROCK:
          {
            val stone = new Stone();
            stone.setPosition(position.add(TileObject.ROCK.getCenterOffset()));
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
    updateBots();
  }

  private void updateBots() {
    for (Bot bot : bots.values()) {
      if (bot.isDisabled()) continue;
      bot.processBot();
    }
  }

  private void updateSafeZone() {
    if (currentTick % GameConstant.TICK_PER_SECOND == 0) {
      // Mỗi giây gây damage một lần
      safeZoneDealDamage();
    }
    if (currentTick % GameConfig.getInstance().getTicksPerSafeZone() != 0) return;
    currentSafeZone++;
    {
      val safeZoneInfo = safeZones.get(currentSafeZone);
      val safeZonePosition = safeZoneInfo.getRight();
      val safeZoneRadius = safeZoneInfo.getLeft().getRadius();
      val builder = new FlatBufferBuilder(0);

      survival2d.flatbuffers.SafeZoneMoveResponse.startSafeZoneMoveResponse(builder);
      val safeZonePositionOffset =
          survival2d.flatbuffers.Vec2.createVec2(
              builder, safeZonePosition.getX(), safeZonePosition.getY());
      survival2d.flatbuffers.SafeZoneMoveResponse.addSafeZone(builder, safeZonePositionOffset);
      survival2d.flatbuffers.SafeZoneMoveResponse.addRadius(builder, safeZoneRadius);
      val responseOffset =
          survival2d.flatbuffers.SafeZoneMoveResponse.endSafeZoneMoveResponse(builder);

      Packet.startPacket(builder);
      Packet.addDataType(builder, PacketData.SafeZoneMoveResponse);
      Packet.addData(builder, responseOffset);
      val packetOffset = Packet.endPacket(builder);
      builder.finish(packetOffset);

      val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
      EzyFoxUtil.stream(bytes, getAllUsernames());
    }
    if (currentSafeZone >= safeZones.size() - 1) {
      return;
    }
    sendNewSafeZoneInfo();
  }

  private void safeZoneDealDamage() {
    for (var i = 0; i <= currentSafeZone; i++) {
      val safeZoneInfo = safeZones.get(i);
      val safeZonePosition = safeZoneInfo.getRight();
      val safeZoneShape = safeZoneInfo.getLeft();
      for (val player : players.values()) {
        if (player.isDestroyed()) continue;
        if (MathUtil.isIntersect(safeZonePosition, safeZoneShape, player.getPosition(), Dot.DOT))
          continue;
        onPlayerTakeDamage(player.getPlayerId(), GameConstant.SAFE_ZONE_DAMAGE);
      }
    }
  }

  private void sendNewSafeZoneInfo() {
    val safeZoneInfo = safeZones.get(currentSafeZone + 1);
    val safeZonePosition = safeZoneInfo.getRight();
    val safeZoneRadius = safeZoneInfo.getLeft().getRadius();
    val builder = new FlatBufferBuilder(0);

    survival2d.flatbuffers.NewSafeZoneResponse.startNewSafeZoneResponse(builder);
    val safeZoneOffset = Vec2.createVec2(builder, safeZonePosition.getX(), safeZonePosition.getY());
    survival2d.flatbuffers.NewSafeZoneResponse.addSafeZone(builder, safeZoneOffset);
    survival2d.flatbuffers.NewSafeZoneResponse.addRadius(builder, safeZoneRadius);
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
          removeMapObject(bullet);
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
    } else if (action instanceof PlayerUseHealItem) {
      val playerUseHealItem = (PlayerUseHealItem) action;
      onPlayerUseHealItem(playerId, playerUseHealItem.getItemId());
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
    } else if (item instanceof BackPackItem) {
      val backPackItem = (BackPackItem) item;
      itemType = survival2d.flatbuffers.Item.BackPackItem;
      survival2d.flatbuffers.BackPackItem.startBackPackItem(builder);
      itemOffset = survival2d.flatbuffers.BackPackItem.endBackPackItem(builder);
    } else {
      log.warn("Unknown item type {}", item.getClass());
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
    EzyFoxUtil.stream(bytes, getUsernamesCanSeeAt(position));
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
        removeMapObject(itemOnMap);

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
        EzyFoxUtil.stream(bytes, getUsernamesCanSeeAt(itemOnMap.getPosition()));
      }
    }
  }

  private void onPlayerReloadWeapon(String playerId) {
    val player = players.get(playerId);
    player.reloadWeapon();
    val builder = new FlatBufferBuilder(0);
    val gun = player.getGun();

    val responseOffset =
        survival2d.flatbuffers.PlayerReloadWeaponResponse.createPlayerReloadWeaponResponse(
            builder, gun.getRemainBullets(), player.getNumBullet());

    Packet.startPacket(builder);
    Packet.addDataType(builder, PacketData.PlayerReloadWeaponResponse);
    Packet.addData(builder, responseOffset);
    val packetOffset = Packet.endPacket(builder);
    builder.finish(packetOffset);

    val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
    EzyFoxUtil.stream(bytes, getAllUsernames());
  }

  private void onPlayerUseHealItem(String playerId, int itemId) {
    val player = players.get(playerId);
    var result = false;
    switch (itemId) {
      case survival2d.flatbuffers.Item.BandageItem:
        result = player.useBandage();
        break;
      case survival2d.flatbuffers.Item.MedKitItem:
        result = player.useMedKit();
        break;
      default:
        log.warn("Not handle use item {}", itemId);
    }
    if (result) {
      val builder = new FlatBufferBuilder(0);

      val responseOffset =
          survival2d.flatbuffers.UseHealItemResponse.createUseHealItemResponse(
              builder, player.getHp(), (byte) itemId, player.getNumItem(itemId));

      Packet.startPacket(builder);
      Packet.addDataType(builder, PacketData.UseHealItemResponse);
      Packet.addData(builder, responseOffset);
      val packetOffset = Packet.endPacket(builder);
      builder.finish(packetOffset);

      val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
      EzyFoxUtil.stream(bytes, getUsernamesCanSeeAt(player.getPosition()));
    }
  }

  public Player getPlayerInfo(String username) {
    return players.get(username);
  }

  public MapObject getObjectsById(int id) {
    return objects.get(id);
  }
}
