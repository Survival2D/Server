package survival2d.ai.bot;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.BehaviorTree;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerReloadWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.item.ItemOnMap;
import survival2d.match.entity.match.Match;
import survival2d.match.entity.obstacle.Container;
import survival2d.match.entity.player.Player;
import survival2d.match.type.GunType;
import survival2d.match.type.WeaponType;
import survival2d.match.util.MatchUtil;

@Getter
@Setter
@Slf4j
public class Bot {
  static final double NUM_TICK_CHANGE_STATUS = 1;

  BehaviorTree behaviorTree;

  private long curTick;
  private double confidencePercent;
  private int controlId;

  private Match match;
  private Player player = null;

  private Vector2 destPos = null;
  private Container destCrate = null;
  private Player destEnemy = null;
  private int destItemId = -1;
  private List<Vector2> path = null;

  private BotBehaviorNode runningNode = null;
  private boolean isEnabled = true;

  private long lastTickAttack = -1;

  public Bot() {
    BTNode botBehavior = new BotBehavior(this);
    behaviorTree = new BehaviorTree(botBehavior);

    curTick = 0;
    lastTickAttack = 0;
    confidencePercent = 1.0;
    controlId = -1;
  }

  public void setMatch(Match match, int id) {
    this.match = match;
    controlId = id;
    this.player = match.getPlayerInfo(controlId);
  }

  public void setRunningNode(BotBehaviorNode runningNode) {
    this.runningNode = runningNode;
  }

  public void processBot() {
    if (this.player == null || this.player.isDestroyed()) return;

    curTick += 1;
    if (curTick % NUM_TICK_CHANGE_STATUS == 0) {
      this.behaviorTree.processTree();
    }
  }

  public Player getPlayerInfo() {
    return player;
  }

  public double getConfidencePercent() {
    return this.confidencePercent;
  }

  public void setConfidencePercent(double confidencePercent) {
    this.confidencePercent = confidencePercent;
  }

  public boolean getNearbyEnemy() {
    if (destEnemy != null && !destEnemy.isDestroyed()) return true;
    destEnemy = null;
    Collection<Player> players = this.match.getNearByPlayer(this.player.getPosition());
    for (Player player : players) {
      if (player.getPlayerId() != controlId) {
        destPos = player.getPosition();
        destEnemy = player;
        return true;
      }
    }
    return false;
  }

  public boolean commandFireEnemy() {
    if (destEnemy.isDestroyed()) {
      destEnemy = null;
      this.commandStopMove();
      return false;
    }
    destPos = destEnemy.getPosition();

    float rotation = this.calculateRotation(this.player.getPosition(), destPos);
    this.player.setRotation(rotation);

    this.commandMove();

    if (!this.isMoving()) {
      destEnemy = null;
      return false;
    }

    boolean isThrew = this.commandThrowAttack();

    if (isThrew) log.debug("Bot {} fire enemy", controlId);

    return isThrew;
  }

  public boolean getNearbyCrate() {
    if (destCrate != null && !destCrate.isDestroyed()) return true;
    destCrate = null;
    Collection<Container> crates = this.match.getNearByContainer(this.player.getPosition());
    for (Container crate : crates) {
      destPos = crate.getPosition();
      destCrate = crate;
      return true;
    }
    return false;
  }

  public boolean commandBreakCrate() {
    if (destCrate.isDestroyed()) {
      destCrate = null;
      this.commandStopMove();
      return false;
    }
    destPos = destCrate.getPosition();

    var rotation = this.calculateRotation(this.player.getPosition(), destPos);
    this.player.setRotation(rotation);

    this.commandMove();

    if (!this.isMoving()) {
      destCrate = null;
      return false;
    }

    boolean isThrew = this.commandThrowAttack();

    if (isThrew) log.debug("Bot {} break crate", controlId);

    return isThrew;
  }

  public boolean getNearbyItem() {
    if (this.match.getObjectsById(destItemId) != null) return true;
    destItemId = -1;
    Collection<ItemOnMap> items = this.match.getNearByItem(this.player.getPosition());
    for (ItemOnMap item : items) {
      destPos = item.getPosition();
      destItemId = item.getId();
      return true;
    }
    return false;
  }

  public void commandTakeItem() {
    MapObject item = this.match.getObjectsById(destItemId);
    if (item == null) {
      this.commandStopMove();
      return;
    }

    log.debug("Bot {} taking item", controlId);

    destPos = item.getPosition();
    this.commandMove();

    if (MatchUtil.isIntersect(item.getShape(), this.player.getShape())) {
      this.match.onReceivePlayerAction(controlId, new PlayerTakeItem());
      this.commandStopMove();
      log.debug("Bot {} taken item", controlId);
    }
  }

  public boolean commandTakeNearbyItem() {
    Collection<ItemOnMap> items = this.match.getNearByItem(this.player.getPosition());
    for (ItemOnMap item : items) {
      if (MatchUtil.isIntersect(item.getShape(), this.player.getShape())) {
        this.match.onReceivePlayerAction(controlId, new PlayerTakeItem());
        log.debug("Bot {} taken a nearby item", controlId);
        return true;
      }
    }
    return false;
  }

  private boolean commandThrowAttack() {
    if (curTick - lastTickAttack < 6) {
      return false;
    }

    int weaponIndex = 0;
    boolean needReload = false;
    for (var gunType: GunType.values()) {
      var numBulletInGun = this.player.getGun(gunType).getRemainBullets();
      var numRemainBullet = this.player.getNumBullet(gunType);
      if (numBulletInGun + numRemainBullet > 0) {
        weaponIndex = gunType.ordinal() + 1;
        if (numBulletInGun == 0) {
          needReload = true;
        }
        break;
      }
    }

    this.match.onReceivePlayerAction(controlId, new PlayerChangeWeapon(weaponIndex));
    if (needReload) {
      this.match.onReceivePlayerAction(controlId, new PlayerReloadWeapon());
    }

    Vector2 attackDirection = destPos.cpy().sub(this.player.getPosition());
    this.match.onPlayerAttack(controlId, attackDirection.nor());

    lastTickAttack = curTick;

    return true;
  }

  public void commandMove() {
    if (destPos == null) return;

    if (this.path == null) {
      this.path = this.match.getPathFromTo(this.player.getPosition(), destPos);
    }

    if (this.path.isEmpty()) {
      this.commandStopMove();
    } else {
      Vector2 nextPosition = this.path.get(0).cpy();
      if (nextPosition.dst(this.player.getPosition())
          <= GameConfig.getInstance().getDefaultPlayerSpeed()) {
        this.path.remove(0);
      }
      if (this.path.isEmpty()) {
        this.commandStopMove();
      } else {
        nextPosition = this.path.get(0).cpy();
        Vector2 moveVector = nextPosition.sub(this.player.getPosition());
        this.match.onPlayerMove(controlId, moveVector, this.player.getRotation());
      }
    }
  }

  private void commandStopMove() {
    destPos = null;
    this.path = null;

    log.debug("{} stop move", controlId);
  }

  public boolean findSafePosition() {
    destPos =
        new Vector2(
            GameConfig.getInstance().getMapWidth() / 2,
            GameConfig.getInstance().getMapHeight() / 2);
    return true;
  }

  public void commandMoveToSafePosition() {
    this.commandMove();

    log.debug("Bot {} move to safe position", controlId);
  }

  public void commandMoveToCenter() {
    if (destPos != null) return;
    destPos =
        new Vector2(
            GameConfig.getInstance().getMapWidth() / 2,
            GameConfig.getInstance().getMapHeight() / 2);
    this.commandMove();

    log.debug("Bot {} move to center", controlId);
  }

  public void commandMoveRandom() {
    Vector2 dest =
        new Vector2(
            GameConfig.getInstance().getMapWidth() / 2,
            GameConfig.getInstance().getMapHeight() / 2);
    Vector2 moveVector = dest.sub(this.player.getPosition());
    this.match.onPlayerMove(controlId, moveVector, this.player.getRotation());

    log.debug("Bot {} move random", controlId);
  }

  public boolean isMoving() {
    return this.destPos != null;
  }

  public boolean isDisabled() {
    return !isEnabled;
  }

  private float calculateRotation(Vector2 originPos, Vector2 destPos) {
    float dx = destPos.x - originPos.x;
    float dy = destPos.y - originPos.y;
    if (dx == 0) {
      if (dy >= 0) return MathUtils.PI / 2;
      else return -MathUtils.PI / 2;
    }
    float angle = MathUtils.atan(dy / dx);
    if (dx < 0) angle = MathUtils.PI + angle;
    if (angle > MathUtils.PI) angle = angle - 2 * MathUtils.PI;
    return angle;
  }
}
