package survival2d.match.entity.weapon;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Movable;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.match.type.GunType;
import survival2d.match.util.MatchUtil;

@Getter
@Setter
@Slf4j
public class Bullet extends BaseMapObject implements MapObject, Movable, Destroyable {

  int id;
  int ownerId;
  Vector2 rawPosition;
  Vector2 direction;
  GunType type;
  boolean isDestroyed;
  Circle shape;

  public Bullet(int ownerId, Vector2 rawPosition, Vector2 direction, GunType type) {
    this.ownerId = ownerId;
    this.rawPosition = rawPosition;
    this.position = rawPosition;
    this.direction = direction;
    this.type = type;
    shape = new Circle(position, GameConfig.getInstance().getBulletRadius());
  }

  public void move() {
    var speed = GameConfig.getInstance().getBulletSpeed();
    moveBy(direction.cpy().scl(speed));
    //    log.info("position: {}", position);
  }

  @Override
  public void setPosition(Vector2 position) {
    super.setPosition(position);
    shape.setPosition(position);
  }

  public boolean isOutOfBound() {
    if (!MatchUtil.isInMap(position)) return true;
    var bulletRange = GameConfig.getInstance().getGunConfigs().get(type).getRange();
    return position.dst2(rawPosition) > bulletRange * bulletRange;
  }
}
