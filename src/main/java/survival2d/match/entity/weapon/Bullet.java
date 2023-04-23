package survival2d.match.entity.weapon;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Movable;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.match.type.BulletType;

@Getter
@Setter
@Slf4j
public class Bullet extends BaseMapObject implements MapObject, Movable, Destroyable {

  int id;
  int ownerId;

  Vector2 position;

  Vector2 rawPosition;
  Vector2 direction;
  BulletType type;
  boolean isDestroyed;
  Circle shape;

  public Bullet(int ownerId, Vector2 rawPosition, Vector2 direction, BulletType type) {
    this.ownerId = ownerId;
    this.rawPosition = rawPosition;
    this.position = rawPosition;
    this.direction = direction; // *speed
    this.type = type;
  }

  public void move() {
    moveBy(direction.scl(type.getSpeed()));
    //    log.info("position: {}", position);
  }

  public boolean isOutOfBound() {
    return position.dst(rawPosition) > type.getMaxRange();
  }
}
