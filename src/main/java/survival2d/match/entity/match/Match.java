package survival2d.match.entity.match;

import static survival2d.match.constant.GameConstant.DOUBLE_MAX_OBJECT_SIZE;
import static survival2d.match.constant.GameConstant.QUAD_MAX_OBJECT_SIZE;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.google.flatbuffers.FlatBufferBuilder;
import java.nio.ByteBuffer;
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
import org.apache.commons.lang3.RandomUtils;
import survival2d.ai.bot.Bot;
import survival2d.data.ServerData;
import survival2d.flatbuffers.BandageItemTable;
import survival2d.flatbuffers.BulletItemTable;
import survival2d.flatbuffers.BulletTable;
import survival2d.flatbuffers.CircleStruct;
import survival2d.flatbuffers.ContainerTable;
import survival2d.flatbuffers.CreateBulletOnMapResponse;
import survival2d.flatbuffers.CreateItemOnMapResponse;
import survival2d.flatbuffers.EndGameResponse;
import survival2d.flatbuffers.GunTable;
import survival2d.flatbuffers.HandTable;
import survival2d.flatbuffers.HelmetItemTable;
import survival2d.flatbuffers.ItemUnion;
import survival2d.flatbuffers.MapObjectTable;
import survival2d.flatbuffers.MapObjectUnion;
import survival2d.flatbuffers.MatchInfoResponse;
import survival2d.flatbuffers.MedKitItemTable;
import survival2d.flatbuffers.NewSafeZoneResponse;
import survival2d.flatbuffers.ObstacleDestroyResponse;
import survival2d.flatbuffers.ObstacleTakeDamageResponse;
import survival2d.flatbuffers.PlayerAttackResponse;
import survival2d.flatbuffers.PlayerChangeWeaponResponse;
import survival2d.flatbuffers.PlayerDeadResponse;
import survival2d.flatbuffers.PlayerInfoResponse;
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
import survival2d.flatbuffers.UseHealItemResponse;
import survival2d.flatbuffers.Vector2Struct;
import survival2d.flatbuffers.VestItemTable;
import survival2d.flatbuffers.WallTable;
import survival2d.flatbuffers.WeaponTable;
import survival2d.flatbuffers.WeaponUnion;
import survival2d.match.action.PlayerAction;
import survival2d.match.action.PlayerAttack;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerMove;
import survival2d.match.action.PlayerReloadWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.match.action.PlayerUseHealItem;
import survival2d.match.config.GameConfig;
import survival2d.match.constant.GameConstant;
import survival2d.match.entity.base.Containable;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.item.BandageItem;
import survival2d.match.entity.item.BulletItem;
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
import survival2d.match.entity.quadtree.QuadTree;
import survival2d.match.entity.quadtree.SpatialPartitionGeneric;
import survival2d.match.entity.weapon.Bullet;
import survival2d.match.entity.weapon.Gun;
import survival2d.match.entity.weapon.Hand;
import survival2d.match.type.GunType;
import survival2d.match.type.ItemType;
import survival2d.match.util.AStar;
import survival2d.match.util.MapGenerator;
import survival2d.match.util.MatchUtil;
import survival2d.match.util.Point;
import survival2d.match.util.TileObject;
import survival2d.match.util.VisionUtil;
import survival2d.network.NetworkUtil;
import survival2d.service.MatchingService;
import survival2d.util.serialize.GsonTransient;

@Getter
@Slf4j
public class Match extends SpatialPartitionGeneric<MapObject> {
  @GsonTransient private final int id;

  @GsonTransient
  private final Map<Integer, Map<Class<? extends PlayerAction>, PlayerAction>> playerRequests =
      new ConcurrentHashMap<>();

  private final Map<Integer, Player> players = new ConcurrentHashMap<>();
  @GsonTransient private final Timer timer = new Timer();
  @GsonTransient private final List<Circle> safeZones = new ArrayList<>();
  @GsonTransient private final List<Vector2> spawnPoints = new ArrayList<>();
  private final Map<Integer, Bot> bots = new ConcurrentHashMap<>();
  private final int NUM_BOTS = 0;
  @GsonTransient int currentSafeZone;
  @GsonTransient private int currentMapObjectId;
  @GsonTransient private TimerTask gameLoopTask;
  @GsonTransient private long currentTick;
  @GsonTransient private AStar aStar;
  @GsonTransient private int[][] grid;

  public Match(int id) {
    this.id = id;
    objects = new ConcurrentHashMap<>();
    quadTree =
        new QuadTree<>(
            0, 0, GameConfig.getInstance().getMapWidth(), GameConfig.getInstance().getMapHeight());
    init();
  }

  public void addPlayer(int teamId, int playerId) {
    var player = new Player(playerId, teamId);
    players.putIfAbsent(playerId, player);
    if (!spawnPoints.isEmpty()) {
      var spawnPoint = spawnPoints.remove(0);
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

  public boolean randomPositionForPlayer(int playerId) {
    var player = players.get(playerId);
    var newPosition =
        MatchUtil.randomPosition(
            Player.BODY_RADIUS,
            GameConfig.getInstance().getMapWidth() - Player.BODY_RADIUS,
            Player.BODY_RADIUS,
            GameConfig.getInstance().getMapHeight() - Player.BODY_RADIUS);

    player.setPosition(newPosition);
    for (var object : getNearBy(newPosition)) {
      if (object instanceof Obstacle obstacle) {
        if (MatchUtil.isIntersect(player.getShape(), obstacle.getShape())) {
          return false;
        }
      }
    }
    return true;
  }

  public Collection<Integer> getAllPlayerIds() {
    return players.keySet();
  }

  // Thường dùng cho obstacle, để gửi thông tin cho user có vùng nhìn ở đây
  public Collection<Integer> getPlayerIdsCanSeeAt(Vector2 position) {
    var result = new HashSet<Integer>();
    getNearByPlayer(position).forEach(p -> result.add(p.getPlayerId()));
    return result;
  }

  public Collection<Integer> getPlayerIdsCanSeeAtAndCheckUnderTree(Vector2 position) {
    var result = new HashSet<Integer>();
    var nearBy = getNearByInVision(position);
    var nearByTrees =
        nearBy.stream()
            .filter(o -> o instanceof Tree)
            .map(o -> (Tree) o)
            .filter(tree -> tree.getFoliage().contains(position))
            .toList();
    nearBy.stream()
        .filter(o -> o instanceof Player)
        .map(o -> (Player) o)
        .filter(
            player ->
                nearByTrees.stream()
                    .allMatch(tree -> tree.getFoliage().contains(player.getPosition())))
        .map(Player::getPlayerId)
        .forEach(result::add);
    return result;
  }

  public boolean isUnderTree(Vector2 position) {
    var nearBy = getNearByInVision(position);
    var nearByTrees =
        nearBy.stream()
            .filter(o -> o instanceof Tree)
            .map(o -> (Tree) o)
            .filter(tree -> tree.getFoliage().contains(position))
            .toList();
    return !nearByTrees.isEmpty();
  }

  public void onReceivePlayerAction(int playerId, PlayerAction action) {
    var player = players.get(playerId);
    if (player.isDestroyed()) {
      log.error("Player {} take action while dead", playerId);
      return;
    }
    var playerActionMap = playerRequests.get(playerId);
    playerActionMap.put(action.getClass(), action);
  }

  public void onPlayerMove(int playerId, Vector2 direction, float rotation) {
    var player = players.get(playerId);
    if (player == null) {
      log.error("player {} is null", playerId);
      return;
    }
    var oldPosition = player.getPosition().cpy();
    if (!direction.isZero()) {
      var unitDirection = direction.nor();
      var moveBy = unitDirection.scl(player.getSpeed());
      player.moveBy(moveBy);
      if (!isValidToMove(player)) {
        log.warn("Player {} can not move to {}", playerId, player.getPosition());
        moveBy.scl(-1);
        player.moveBy(moveBy);
      }
    }
    player.setRotation(rotation);
    onMapObjectMove(player);
    var newPosition = player.getPosition();
    var newMapObjects = new HashSet<MapObject>();
    if (!VisionUtil.isSameVisionX(oldPosition, newPosition)) {
      var boundary = VisionUtil.getBoundaryXAxis(oldPosition, newPosition);
      var query = quadTree.query(boundary);
      newMapObjects.addAll(query);
    }
    if (!VisionUtil.isSameVisionY(oldPosition, newPosition)) {
      var boundary = VisionUtil.getBoundaryYAxis(oldPosition, newPosition);
      var query = quadTree.query(boundary);
      newMapObjects.addAll(query);
    }
    newMapObjects.removeIf(
        (object) -> object instanceof Player enemy && isUnderTree(enemy.getPosition()));
    if (!newMapObjects.isEmpty()) {
      var data = getMatchInfoData(newMapObjects);
      NetworkUtil.sendResponse(playerId, data);
    }

    var builder = new FlatBufferBuilder(0);

    PlayerMoveResponse.startPlayerMoveResponse(builder);
    PlayerMoveResponse.addPlayerId(builder, playerId);
    PlayerMoveResponse.addRotation(builder, rotation);
    var positionOffset =
        Vector2Struct.createVector2Struct(builder, player.getPosition().x, player.getPosition().y);
    PlayerMoveResponse.addPosition(builder, positionOffset);
    var responseOffset = PlayerMoveResponse.endPlayerMoveResponse(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.PlayerMoveResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    var userReceivePackets = new HashSet<Integer>();
    userReceivePackets.addAll(getPlayerIdsCanSeeAtAndCheckUnderTree(oldPosition));
    userReceivePackets.addAll(getPlayerIdsCanSeeAtAndCheckUnderTree(player.getPosition()));
    NetworkUtil.sendResponse(userReceivePackets, builder.dataBuffer());
  }

  private Collection<MapObject> getNearBy(Vector2 position) {
    return quadTree.query(
        new Rectangle(
            position.x - DOUBLE_MAX_OBJECT_SIZE,
            position.y - DOUBLE_MAX_OBJECT_SIZE,
            QUAD_MAX_OBJECT_SIZE,
            QUAD_MAX_OBJECT_SIZE));
  }

  private Collection<MapObject> getObjectsIntersectWithBoundary(Shape2D boundary) {
    return quadTree.query(boundary);
  }

  private Collection<MapObject> getNearByInVision(Vector2 position) {
    var width = GameConfig.getInstance().getPlayerViewWidth();
    var height = GameConfig.getInstance().getPlayerViewHeight();
    var boundary = new Rectangle(position.x - width / 2, position.y - height / 2, width, height);
    return quadTree.query(boundary);
  }

  public Collection<Container> getNearByContainer(Vector2 position) {
    return getNearByInVision(position).stream()
        .filter(object -> object instanceof Container)
        .map(object -> (Container) object)
        .collect(Collectors.toList());
  }

  public Collection<ItemOnMap> getNearByItem(Vector2 position) {
    return getNearByInVision(position).stream()
        .filter(object -> object instanceof ItemOnMap)
        .map(object -> (ItemOnMap) object)
        .collect(Collectors.toList());
  }

  public Collection<Player> getNearByPlayer(Vector2 position) {
    return getNearByInVision(position).stream()
        .filter(object -> object instanceof Player)
        .map(object -> (Player) object)
        .collect(Collectors.toList());
  }

  private boolean isValidPositionForPlayer(Vector2 position) {
    return position.x - Player.BODY_RADIUS >= 0
        && position.x + Player.BODY_RADIUS <= GameConfig.getInstance().getMapWidth()
        && position.y - Player.BODY_RADIUS >= 0
        && position.y + Player.BODY_RADIUS <= GameConfig.getInstance().getMapHeight();
  }

  private boolean isValidToMove(MapObject mapObject) {
    var isCollide = isCollisionWithObstacle(mapObject);
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
              var obstacle = (Obstacle) object;
              return MatchUtil.isIntersect(mapObject.getShape(), obstacle.getShape());
            });
  }

  public void onPlayerAttack(int playerId, Vector2 direction) {
    var player = players.get(playerId);
    var currentWeapon = player.getCurrentWeapon();
    if (currentWeapon instanceof Hand hand) {
      createDamage(
          playerId,
          new Circle(
              player
                  .getPosition()
                  .cpy()
                  .add(
                      direction.scl(
                          GameConfig.getInstance().getPlayerBodyRadius()
                              + hand.getConfig().getRange())),
              hand.getConfig().getRange()),
          hand.getConfig().getDamage());
    } else if (currentWeapon instanceof Gun gun) {
      if (!gun.isReadyToShoot()) {
        return;
      }
      gun.reduceAmmo();
      if (gun.getType() == GunType.SHOTGUN) {
        for (int i = 0; i < GameConfig.getInstance().getShotGunLines(); i++) {
          var randomDirection = new Vector2(direction);
          randomDirection.rotateDeg(
              RandomUtils.nextFloat(0, GameConfig.getInstance().getShotGunSpread())
                  - GameConfig.getInstance().getHalfShotGunSpread());
          createBullet(
              playerId,
              player
                  .getPosition()
                  .cpy()
                  .add(
                      randomDirection
                          .cpy()
                          .scl(Player.BODY_RADIUS + GameConstant.INITIAL_BULLET_DISTANCE)),
              randomDirection,
              gun.getType());
        }
      } else {
        createBullet(
            playerId,
            player
                .getPosition()
                .cpy()
                .add(
                    direction.cpy().scl(Player.BODY_RADIUS + GameConstant.INITIAL_BULLET_DISTANCE)),
            direction,
            gun.getType());
      }
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
      var x = (int) (mapObject.getPosition().x / MapGenerator.TILE_SIZE);
      var y = (int) (mapObject.getPosition().y / MapGenerator.TILE_SIZE);
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

  public List<Vector2> getPathFromTo(Vector2 from, Vector2 to) {
    var fromX = (int) (from.x / MapGenerator.TILE_SIZE);
    var fromY = (int) (from.y / MapGenerator.TILE_SIZE);
    var toX = (int) (to.x / MapGenerator.TILE_SIZE);
    var toY = (int) (to.y / MapGenerator.TILE_SIZE);
    return aStar.aStarSearch(new Point(fromX, fromY), new Point(toX, toY)).stream()
        .map(
            point -> {
              var x = point.getX() * MapGenerator.TILE_SIZE + MapGenerator.TILE_SIZE / 2;
              var y = point.getY() * MapGenerator.TILE_SIZE + MapGenerator.TILE_SIZE / 2;
              return new Vector2(x, y);
            })
        .collect(Collectors.toList());
  }

  public void setPlayerAutoPlay(int playerId, boolean enable) {
    var bot =
        bots.computeIfAbsent(
            playerId,
            (k) -> {
              Bot b = new Bot();
              b.setMatch(this, playerId);
              b.setConfidencePercent(1.0);
              return b;
            });
    bot.setEnabled(enable);
  }

  public void onMapObjectMove(MapObject object) {
    quadTree.update(object);
  }

  public void createDamage(int playerId, Circle damageShape, double damage) {
    var builder = new FlatBufferBuilder(0);

    PlayerAttackResponse.startPlayerAttackResponse(builder);
    PlayerAttackResponse.addPlayerId(builder, playerId);
    var positionOffset = Vector2Struct.createVector2Struct(builder, damageShape.x, damageShape.y);
    PlayerMoveResponse.addPosition(builder, positionOffset);
    var responseOffset = PlayerAttackResponse.endPlayerAttackResponse(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.PlayerAttackResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    NetworkUtil.sendResponse(
        getPlayerIdsCanSeeAt(new Vector2(damageShape.x, damageShape.y)), builder.dataBuffer());

    makeDamage(playerId, damageShape, damage);
  }

  public void makeDamage(int playerId, Shape2D shape, double damage) {
    var currentPlayer = players.get(playerId);
    var intersectedObjects = getObjectsIntersectWithBoundary(shape);
    for (var object : intersectedObjects) {
      if (object instanceof Player player) {
        // Cùng team thì không tính damage
        if (player.getTeam() == currentPlayer.getTeam()) continue;
        // Chính người chơi đó thì mới không tính damage
        // if (Objects.equals(player.getPlayerId(), playerId)) continue;
        if (player.isDestroyed()) continue;
        if (MatchUtil.isIntersect(player.getShape(), shape)) {
          var isHeadshot = MatchUtil.isIntersect(player.getHead(), shape);
          var damageMultiple = isHeadshot ? GameConstant.HEADSHOT_DAMAGE : GameConstant.BODY_DAMAGE;
          var totalDamage = damage * damageMultiple;
          var reduceDamage =
              isHeadshot
                  ? GameConfig.getInstance()
                      .getHelmetReduceDamagePercent()
                      .getOrDefault(player.getHelmetType(), 0.0)
                  : GameConfig.getInstance()
                      .getVestReduceDamagePercent()
                      .getOrDefault(player.getVestType(), 0.0);
          var finalDamage = totalDamage - reduceDamage;
          onPlayerTakeDamage(player.getPlayerId(), finalDamage);
        }
      } else if (object instanceof Obstacle obstacle) {
        if (!(obstacle instanceof Destroyable destroyable)) {
          continue;
        }
        if (destroyable.isDestroyed()) {
          continue;
        }
        var hasHp = (HasHp) obstacle;
        if (MatchUtil.isIntersect(obstacle.getShape(), shape)) {
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

            NetworkUtil.sendResponse(
                getPlayerIdsCanSeeAt(obstacle.getPosition()), builder.dataBuffer());
          }
          if (destroyable.isDestroyed()) {
            log.info("Obstacle {} destroyed", obstacle.getId());
            var builder = new FlatBufferBuilder(0);

            var responseOffset =
                ObstacleDestroyResponse.createObstacleDestroyResponse(builder, obstacle.getId());
            removeMapObject(obstacle);

            Response.startResponse(builder);
            Response.addResponseType(builder, ResponseUnion.ObstacleDestroyResponse);
            Response.addResponse(builder, responseOffset);
            var packetOffset = Response.endResponse(builder);
            builder.finish(packetOffset);

            NetworkUtil.sendResponse(
                getPlayerIdsCanSeeAt(obstacle.getPosition()), builder.dataBuffer());

            if (obstacle instanceof Containable containable) {
              for (var item : containable.getItems()) {
                createItemOnMap(item, obstacle.getPosition());
              }
            }
          }
        }
      }
    }
  }

  private void onPlayerTakeDamage(int playerId, double damage) {
    var player = players.get(playerId);
    player.reduceHp(damage);
    {
      var builder = new FlatBufferBuilder(0);

      PlayerTakeDamageResponse.startPlayerTakeDamageResponse(builder);
      PlayerTakeDamageResponse.addPlayerId(builder, playerId);
      PlayerTakeDamageResponse.addRemainHp(builder, player.getHp());
      var responseOffset = PlayerTakeDamageResponse.endPlayerTakeDamageResponse(builder);

      Response.startResponse(builder);
      Response.addResponseType(builder, ResponseUnion.PlayerTakeDamageResponse);
      Response.addResponse(builder, responseOffset);
      var packetOffset = Response.endResponse(builder);
      builder.finish(packetOffset);

      NetworkUtil.sendResponse(getPlayerIdsCanSeeAt(player.getPosition()), builder.dataBuffer());
    }
    if (player.isDestroyed()) {
      var builder = new FlatBufferBuilder(0);

      PlayerDeadResponse.startPlayerDeadResponse(builder);
      PlayerDeadResponse.addPlayerId(builder, playerId);
      var responseOffset = PlayerDeadResponse.endPlayerDeadResponse(builder);

      Response.startResponse(builder);
      Response.addResponseType(builder, ResponseUnion.PlayerDeadResponse);
      Response.addResponse(builder, responseOffset);
      var packetOffset = Response.endResponse(builder);
      builder.finish(packetOffset);

      NetworkUtil.sendResponse(getAllPlayerIds(), builder.dataBuffer());

      checkEndGame();
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

      NetworkUtil.sendResponse(getAllPlayerIds(), builder.dataBuffer());

      stop();
    }
  }

  public void createBullet(int playerId, Vector2 position, Vector2 direction, GunType type) {
    var bullet = new Bullet(playerId, position, direction, type);
    addMapObject(bullet);

    var builder = new FlatBufferBuilder(0);

    BulletTable.startBulletTable(builder);
    BulletTable.addType(builder, (byte) type.ordinal());
    BulletTable.addId(builder, bullet.getId());
    var positionOffset = Vector2Struct.createVector2Struct(builder, position.x, position.y);
    BulletTable.addPosition(builder, positionOffset);
    var directionOffset = Vector2Struct.createVector2Struct(builder, direction.x, direction.y);
    BulletTable.addDirection(builder, directionOffset);
    BulletTable.addOwner(builder, playerId);
    var bulletOffset = BulletTable.endBulletTable(builder);
    var responseOffset =
        CreateBulletOnMapResponse.createCreateBulletOnMapResponse(builder, bulletOffset);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.CreateBulletOnMapResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    NetworkUtil.sendResponse(getAllPlayerIds(), builder.dataBuffer());
  }

  public void responseMatchInfoOnStart(int playerId) {
    //    final byte[] bytes = getMatchInfoData(objects.values());
    var player = players.get(playerId);
    var objects = new HashSet<MapObject>();
    objects.addAll(quadTree.query(player.getPlayerView()));
    objects.addAll(players.values());
    var data = getMatchInfoData(objects);
    NetworkUtil.sendResponse(playerId, data);
  }

  public void responsePlayerInfo(int playerId) {
    var player = players.get(playerId);
    var builder = new FlatBufferBuilder(0);

    int[] weaponsOffsets = new int[player.getWeapons().size()];
    for (int i = 0; i < player.getWeapons().size(); i++) {
      var weapon = player.getWeapons().get(i);
      if (weapon instanceof Hand hand) {
        HandTable.startHandTable(builder);
        var handData = HandTable.endHandTable(builder);
        weaponsOffsets[i] = WeaponTable.createWeaponTable(builder, WeaponUnion.HandTable, handData);
      } else if (weapon instanceof Gun gun) {
        var gunData = GunTable.createGunTable(builder, gun.getFbsGunType(), gun.getRemainBullets());
        weaponsOffsets[i] = WeaponTable.createWeaponTable(builder, WeaponUnion.GunTable, gunData);
      }
    }
    var bullets = player.getBullets().entrySet();
    int[] bulletsOffsets = new int[bullets.size()];
    int bulletIndex = 0;
    for (var bullet : bullets) {
      bulletsOffsets[bulletIndex] =
          BulletItemTable.createBulletItemTable(
              builder, bullet.getKey().toFbsGunType(), bullet.getValue());
      bulletIndex++;
    }

    var weaponsOffset = PlayerInfoResponse.createWeaponsVector(builder, weaponsOffsets);
    var bulletsOffset = PlayerInfoResponse.createBulletsVector(builder, bulletsOffsets);
    var responseOffset =
        PlayerInfoResponse.createPlayerInfoResponse(
            builder, player.getHp(), weaponsOffset, bulletsOffset);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.PlayerInfoResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    NetworkUtil.sendResponse(playerId, builder.dataBuffer());
  }

  private ByteBuffer getMatchInfoInBoundary(Shape2D boundary) {
    var objects = quadTree.query(boundary);
    return getMatchInfoData(objects);
  }

  public ByteBuffer getMatchInfoData(Collection<MapObject> matchMapObjects) {
    var builder = new FlatBufferBuilder(0);
    builder.clear();

    final int responseOffset = putMatchInfoData(builder, matchMapObjects);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.MatchInfoResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    return builder.dataBuffer();
  }

  public int putMatchInfoData(FlatBufferBuilder builder) {
    return putMatchInfoData(builder, objects.values());
  }

  public int putMatchInfoData(FlatBufferBuilder builder, Collection<MapObject> matchMapObjects) {
    var matchPlayers = // players.values();
        matchMapObjects.stream()
            .filter(o -> o instanceof Player)
            .map(player -> (Player) player)
            .toList();
    int[] playerOffsets = new int[matchPlayers.size()];
    var players = matchPlayers.toArray(new Player[0]);
    for (int i = 0; i < players.length; i++) {
      var player = players[i];
      var user = ServerData.getInstance().getUser(player.getPlayerId());
      var name = user != null ? user.getName() : "User " + player.getPlayerId();

      var playerName = builder.createString(name);
      PlayerTable.startPlayerTable(builder);
      PlayerTable.addPlayerId(builder, player.getPlayerId());

      PlayerTable.addPlayerName(builder, playerName);
      var positionOffset =
          Vector2Struct.createVector2Struct(
              builder, player.getPosition().x, player.getPosition().y);
      PlayerTable.addPosition(builder, positionOffset);
      PlayerTable.addRotation(builder, player.getRotation());
      PlayerTable.addTeam(builder, player.getTeam());
      playerOffsets[i] = PlayerTable.endPlayerTable(builder);
    }

    var mapObjects =
        matchMapObjects.stream()
            .filter(mapObject -> mapObject instanceof Obstacle || mapObject instanceof Item)
            .toArray(MapObject[]::new);
    int[] mapObjectOffsets = new int[mapObjects.length];
    for (int i = 0; i < mapObjects.length; i++) {
      var object = mapObjects[i];
      var objectDataOffset = 0;
      byte objectDataType = 0;

      if (object instanceof BulletItem bulletItem) {
        objectDataType = MapObjectUnion.BulletItemTable;
        BulletItemTable.startBulletItemTable(builder);
        BulletItemTable.addType(builder, (byte) bulletItem.getGunType().ordinal());
        objectDataOffset = BulletItemTable.endBulletItemTable(builder);
        //        var bulletItemOffset = BulletItem.endBulletItem(builder);
        //        MapObject.addResponse(builder, bulletItemOffset);
      } else if (object instanceof HelmetItem helmetItem) {
        objectDataType = MapObjectUnion.HelmetItemTable;
        objectDataOffset =
            HelmetItemTable.createHelmetItemTable(
                builder, (byte) helmetItem.getHelmetType().ordinal());
      } else if (object instanceof VestItem vestItem) {
        objectDataType = MapObjectUnion.VestItemTable;
        objectDataOffset =
            VestItemTable.createVestItemTable(builder, (byte) vestItem.getVestType().ordinal());
      } else if (object instanceof MedKitItem medKitItem) {
        objectDataType = MapObjectUnion.MedKitItemTable;
        MedKitItemTable.startMedKitItemTable(builder);
        objectDataOffset = MedKitItemTable.endMedKitItemTable(builder);
      } else if (object instanceof BandageItem bandageItem) {
        objectDataType = MapObjectUnion.BandageItemTable;
        BandageItemTable.startBandageItemTable(builder);
        objectDataOffset = BandageItemTable.endBandageItemTable(builder);
      } else if (object instanceof Tree tree) {
        objectDataType = MapObjectUnion.TreeTable;
        TreeTable.startTreeTable(builder);
        objectDataOffset = TreeTable.endTreeTable(builder);
        //        var treeOffset = Tree.endTree(builder);
        //        MapObject.addResponse(builder, treeOffset);
      } else if (object instanceof Container container) {
        objectDataType = MapObjectUnion.ContainerTable;
        ContainerTable.startContainerTable(builder);
        objectDataOffset = ContainerTable.endContainerTable(builder);
        //        var containerOffset = Container.endContainer(builder);
        //        MapObject.addResponse(builder, containerOffset);
      } else if (object instanceof Stone stone) {
        objectDataType = MapObjectUnion.StoneTable;
        StoneTable.startStoneTable(builder);
        objectDataOffset = StoneTable.endStoneTable(builder);
        //        var stoneOffset = Stone.endStone(builder);
        //        MapObject.addResponse(builder, stoneOffset);
      } else if (object instanceof Wall wall) {
        objectDataType = MapObjectUnion.WallTable;
        WallTable.startWallTable(builder);
        objectDataOffset = WallTable.endWallTable(builder);
        //        var wallOffset = Wall.endWall(builder);
        //        MapObject.addResponse(builder, wallOffset);
      }
      MapObjectTable.startMapObjectTable(builder);
      MapObjectTable.addId(builder, object.getId());
      var positionOffset =
          Vector2Struct.createVector2Struct(
              builder, object.getPosition().x, object.getPosition().y);
      MapObjectTable.addPosition(builder, positionOffset);
      MapObjectTable.addData(builder, objectDataOffset);
      MapObjectTable.addDataType(builder, objectDataType);
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
    return MatchInfoResponse.endMatchInfoResponse(builder);
  }

  public void responseMatchInfoOnStart() {
    //    var bytes = getMatchInfoData(objects.values());
    //    EzyFoxUtil.stream(bytes, getAllUsernames());
    for (var player : players.values()) {
      responseMatchInfoOnStart(player.getPlayerId());
      responsePlayerInfo(player.getPlayerId());
    }
    sendNewSafeZoneInfo();
  }

  public void onPlayerSwitchWeapon(int playerId, int weaponId) {
    var player = players.get(playerId);
    if (player == null) {
      log.error("player {} is null", playerId);
      return;
    }
    player.switchWeapon(weaponId);
    var builder = new FlatBufferBuilder(0);

    var responseOffset =
        PlayerChangeWeaponResponse.createPlayerChangeWeaponResponse(
            builder, playerId, (byte) weaponId);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.PlayerChangeWeaponResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    NetworkUtil.sendResponse(getAllPlayerIds(), builder.dataBuffer());
  }

  public void init() {
    // FIXME:
    var testPing = false;
    if (!testPing) {
      timer.schedule(
          new TimerTask() {

            public void run() {
              start();
            }
          },
          3000);
    }
    initSafeZones();
    initObstacles();
    initBots();
  }

  private void initBots() {
    for (int i = 0; i < NUM_BOTS; i++) {
      this.addPlayer(-1 - i, i);

      Bot bot = new Bot();
      bot.setMatch(this, i);
      bot.setConfidencePercent(1.0);
      bots.putIfAbsent(i, bot);
    }
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
      var newPosition =
          MatchUtil.randomPosition(
              previousSafeZone.x - deltaRadius,
              previousSafeZone.x + deltaRadius,
              previousSafeZone.y - deltaRadius,
              previousSafeZone.y + deltaRadius);
      safeZones.add(new Circle(newPosition, radius));
    }
    currentSafeZone = -1;
  }

  public void stop() {
    gameLoopTask.cancel();
    timer.cancel();
    MatchingService.getInstance().destroyMatch(id);
  }

  private void initObstacles() {
    var generateResult = MapGenerator.generateMap();
    grid = generateResult.getTiles();
    aStar = new AStar(MapGenerator.MAP_WIDTH, MapGenerator.MAP_HEIGHT, grid);
    for (var tileObject : generateResult.getMapObjects()) {
      var position =
          new Vector2(
              tileObject.getPosition().getX() * MapGenerator.TILE_SIZE,
              tileObject.getPosition().getY() * MapGenerator.TILE_SIZE);
      switch (tileObject.getType()) {
        case PLAYER -> {
          spawnPoints.add(position.add(TileObject.PLAYER.getCenterOffset()));
        }
        case ITEM -> {
          var item = new ItemOnMap(ItemFactory.randomItem(), position);
          addMapObject(item);
        }
        case WALL -> {
          var wall = new Wall();
          wall.setPosition(position);
          addMapObject(wall);
        }
        case TREE -> {
          var tree = new Tree();
          tree.setPosition(position.add(TileObject.TREE.getCenterOffset()));
          addMapObject(tree);
        }
        case BOX -> {
          var container = new Container();
          container.setPosition(position);
          container.setItems(ItemFactory.randomItems());
          addMapObject(container);
        }
        case ROCK -> {
          var stone = new Stone();
          stone.setPosition(position.add(TileObject.ROCK.getCenterOffset()));
          addMapObject(stone);
        }
      }
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

    NetworkUtil.sendResponse(getAllPlayerIds(), builder.dataBuffer());
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
    sendSafeZoneMove();
    if (currentSafeZone >= safeZones.size() - 1) {
      return;
    }
    sendNewSafeZoneInfo();
  }

  private void sendSafeZoneMove() {
    var safeZone = safeZones.get(currentSafeZone);
    var builder = new FlatBufferBuilder(0);

    SafeZoneMoveResponse.startSafeZoneMoveResponse(builder);
    var safeZonePositionOffset =
        CircleStruct.createCircleStruct(builder, safeZone.x, safeZone.y, safeZone.radius);
    SafeZoneMoveResponse.addSafeZone(builder, safeZonePositionOffset);
    var responseOffset = SafeZoneMoveResponse.endSafeZoneMoveResponse(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.SafeZoneMoveResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    NetworkUtil.sendResponse(getAllPlayerIds(), builder.dataBuffer());
  }

  private void safeZoneDealDamage() {
    for (var i = 0; i <= currentSafeZone; i++) {
      var safeZone = safeZones.get(i);
      for (var player : players.values()) {
        if (player.isDestroyed()) continue;
        if (safeZone.contains(player.getPosition())) continue;
        onPlayerTakeDamage(player.getPlayerId(), GameConstant.SAFE_ZONE_DAMAGE);
      }
    }
  }

  private void sendNewSafeZoneInfo() {
    var safeZone = safeZones.get(currentSafeZone + 1);
    var builder = new FlatBufferBuilder(0);

    NewSafeZoneResponse.startNewSafeZoneResponse(builder);
    var safeZoneOffset =
        CircleStruct.createCircleStruct(builder, safeZone.x, safeZone.y, safeZone.radius);
    NewSafeZoneResponse.addSafeZone(builder, safeZoneOffset);
    var responseOffset = NewSafeZoneResponse.endNewSafeZoneResponse(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.NewSafeZoneResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    NetworkUtil.sendResponse(getAllPlayerIds(), builder.dataBuffer());
  }

  private void updateMapObjects() {
    for (var mapObject : objects.values()) {
      if (mapObject instanceof Bullet bullet) {
        bullet.move();
        onMapObjectMove(bullet);

        var ownerId = bullet.getOwnerId();
        var owner = players.get(ownerId);
        var bulletPosition = bullet.getPosition();
        log.info("bullet position {}", bulletPosition);
        for (var object : getNearBy(bulletPosition)) {
          if (object instanceof Player player) {
            log.warn("player's team {}, owner's team {}", player.getTeam(), owner.getTeam());
            log.warn("player's hp {}, player is dead {}", player.getHp(), player.isDestroyed());
            if (player.getTeam() == owner.getTeam() || player.isDestroyed()) {
              continue;
            }
            log.warn("player position {}", player.getPosition());
            if (MatchUtil.isIntersect(player.getShape(), bullet.getShape())) {
              log.warn("player {} is hit by bullet {}", player.getPlayerId(), bullet.getId());
              makeDamage(
                  ownerId,
                  new Circle(
                      bullet.getPosition(), GameConfig.getInstance().getBulletDamageRadius()),
                  GameConfig.getInstance().getGunConfigs().get(bullet.getType()).getDamage());
              bullet.setDestroyed(true);
            }
          } else {
            if (object == mapObject) {
              continue; // Chính nó
            }
            if (object instanceof Destroyable destroyable) {
              if (destroyable.isDestroyed()) {
                continue;
              }
            }
            if (MatchUtil.isIntersect(object.getShape(), bullet.getShape())) {
              log.info("object {} is hit by bullet {}", object.getId(), bullet.getId());
              makeDamage(
                  ownerId,
                  new Circle(
                      bullet.getPosition(), GameConfig.getInstance().getBulletDamageRadius()),
                  GameConfig.getInstance().getGunConfigs().get(bullet.getType()).getDamage());
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
    for (var player : players.values()) {
      var playerActionMap = playerRequests.get(player.getPlayerId());
      if (playerActionMap == null) {
        continue;
      }
      for (var action : playerActionMap.values()) {
        handlePlayerAction(player.getPlayerId(), action);
      }
      playerActionMap.clear();
    }
  }

  private void handlePlayerAction(int playerId, PlayerAction action) {
    if (action instanceof PlayerMove playerMove) {
      onPlayerMove(playerId, playerMove.direction(), playerMove.rotation());
    } else if (action instanceof PlayerAttack) {
      var player = players.get(playerId);
      onPlayerAttack(playerId, player.getAttackDirection());
    } else if (action instanceof PlayerChangeWeapon playerChangeWeapon) {
      onPlayerSwitchWeapon(playerId, playerChangeWeapon.weaponIndex());
    } else if (action instanceof PlayerReloadWeapon) {
      onPlayerReloadWeapon(playerId);
    } else if (action instanceof PlayerTakeItem) {
      onPlayerTakeItem(playerId);
    } else if (action instanceof PlayerUseHealItem playerUseHealItem) {
      onPlayerUseHealItem(playerId, playerUseHealItem.itemType());
    }
  }

  private void createItemOnMap(Item item, Vector2 rawPosition) {
    var randomNeighborPosition = MatchUtil.randomPosition(-100, 100, -100, 100);
    var position = rawPosition.add(randomNeighborPosition);
    var itemOnMap = new ItemOnMap(item, position);
    addMapObject(itemOnMap);
    var builder = new FlatBufferBuilder(0);
    var itemOffset = 0;
    byte itemType = 0;
    if (item instanceof BulletItem bulletItem) {
      itemType = ItemUnion.BulletItemTable;
      itemOffset =
          BulletItemTable.createBulletItemTable(
              builder, (byte) bulletItem.getGunType().ordinal(), bulletItem.getNumBullet());
    } else if (item instanceof HelmetItem helmetItem) {
      itemType = ItemUnion.HelmetItemTable;
      itemOffset =
          HelmetItemTable.createHelmetItemTable(
              builder, (byte) helmetItem.getHelmetType().ordinal());
    } else if (item instanceof VestItem vestItem) {
      itemType = ItemUnion.VestItemTable;
      itemOffset =
          VestItemTable.createVestItemTable(builder, (byte) vestItem.getVestType().ordinal());
    } else if (item instanceof MedKitItem medKitItem) {
      itemType = ItemUnion.MedKitItemTable;
      MedKitItemTable.startMedKitItemTable(builder);
      itemOffset = MedKitItemTable.endMedKitItemTable(builder);
    } else if (item instanceof BandageItem bandageItem) {
      itemType = ItemUnion.BandageItemTable;
      BandageItemTable.startBandageItemTable(builder);
      itemOffset = BandageItemTable.endBandageItemTable(builder);
    } else {
      log.warn("Unknown item type {}", item.getClass());
    }

    CreateItemOnMapResponse.startCreateItemOnMapResponse(builder);
    CreateItemOnMapResponse.addId(builder, itemOnMap.getId());
    CreateItemOnMapResponse.addItem(builder, itemOffset);
    CreateItemOnMapResponse.addItemType(builder, itemType);
    var positionOffset = Vector2Struct.createVector2Struct(builder, position.x, position.y);
    CreateItemOnMapResponse.addPosition(builder, positionOffset);
    var rawPositionOffset =
        Vector2Struct.createVector2Struct(builder, rawPosition.x, rawPosition.y);
    CreateItemOnMapResponse.addRawPosition(builder, rawPositionOffset);
    var responseOffset = CreateItemOnMapResponse.endCreateItemOnMapResponse(builder);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.CreateItemOnMapResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    NetworkUtil.sendResponse(getPlayerIdsCanSeeAt(position), builder.dataBuffer());
  }

  private void onPlayerTakeItem(int playerId) {
    var player = players.get(playerId);
    for (var object : getNearBy(player.getPosition())) {
      if (!(object instanceof ItemOnMap itemOnMap)) {
        continue;
      }
      if (MatchUtil.isIntersect(player.getShape(), itemOnMap.getShape())) {
        player.takeItem(itemOnMap.getItem());
        removeMapObject(itemOnMap);

        var builder = new FlatBufferBuilder(0);

        var responseOffset =
            PlayerTakeItemResponse.createPlayerTakeItemResponse(builder, playerId, object.getId());

        Response.startResponse(builder);
        Response.addResponseType(builder, ResponseUnion.PlayerTakeItemResponse);
        Response.addResponse(builder, responseOffset);
        var packetOffset = Response.endResponse(builder);
        builder.finish(packetOffset);

        NetworkUtil.sendResponse(
            getPlayerIdsCanSeeAt(itemOnMap.getPosition()), builder.dataBuffer());
      }
    }
  }

  private void onPlayerReloadWeapon(int playerId) {
    var player = players.get(playerId);
    player.reloadWeapon();
    var weapon = player.getCurrentWeapon();
    if (!(weapon instanceof Gun gun)) {
      return;
    }
    var builder = new FlatBufferBuilder(0);

    var responseOffset =
        PlayerReloadWeaponResponse.createPlayerReloadWeaponResponse(
            builder,
            gun.getFbsGunType(),
            gun.getRemainBullets(),
            player.getNumBullet(gun.getType()));

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.PlayerReloadWeaponResponse);
    Response.addResponse(builder, responseOffset);
    var packetOffset = Response.endResponse(builder);
    builder.finish(packetOffset);

    NetworkUtil.sendResponse(getAllPlayerIds(), builder.dataBuffer());
  }

  private void onPlayerUseHealItem(int playerId, ItemType itemType) {
    var player = players.get(playerId);
    var result = false;
    byte itemId = -1;
    switch (itemType) {
      case BANDAGE -> {
        result = player.useBandage();
        itemId = ItemUnion.BandageItemTable;
      }
      case MEDKIT -> {
        result = player.useMedKit();
        itemId = ItemUnion.MedKitItemTable;
      }
      default -> log.warn("Not handle use item {}", itemType);
    }
    if (result) {
      var builder = new FlatBufferBuilder(0);

      var responseOffset =
          UseHealItemResponse.createUseHealItemResponse(
              builder, player.getHp(), itemId, player.getNumItem(itemType));

      Response.startResponse(builder);
      Response.addResponseType(builder, ResponseUnion.UseHealItemResponse);
      Response.addResponse(builder, responseOffset);
      var packetOffset = Response.endResponse(builder);
      builder.finish(packetOffset);

      NetworkUtil.sendResponse(getPlayerIdsCanSeeAt(player.getPosition()), builder.dataBuffer());
    }
  }

  public Player getPlayerInfo(int playerId) {
    return players.get(playerId);
  }

  public MapObject getObjectsById(int id) {
    return objects.get(id);
  }
}
