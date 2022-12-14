package survival2d.match.entity.weapon;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.Dot;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Movable;
import survival2d.match.entity.config.BulletType;

@Data
@Slf4j
public class Bullet implements MapObject, Movable, Destroyable {

  int id;
  String ownerId;

  Vector2D position;

  Vector2D rawPosition;
  Vector2D direction;
  BulletType type;
  boolean isDestroyed;
  Dot shape = new Dot();

  public Bullet(String ownerId, Vector2D rawPosition, Vector2D direction, BulletType type) {
    this.ownerId = ownerId;
    this.rawPosition = rawPosition;
    this.position = rawPosition;
    this.direction = direction; // *speed
    this.type = type;
  }

  public void move() {
    moveBy(direction.scalarMultiply(type.getSpeed()));
    //    log.info("position: {}", position);
  }

  public boolean isOutOfBound() {
    return position.distance(rawPosition) > type.getMaxRange();
  }
}
