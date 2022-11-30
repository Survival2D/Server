package survival2d.game.entity;

import survival2d.game.entity.base.Destroyable;
import survival2d.game.entity.base.Dot;
import survival2d.game.entity.base.MapObject;
import survival2d.game.entity.base.Movable;
import survival2d.game.entity.config.BulletType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.math.Vector2D;


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
    this.direction = direction; //*speed
    this.type = type;
  }

  public void move() {
    moveBy(direction.multiply(type.getSpeed()));
//    log.info("position: {}", position);
  }


  public boolean isOutOfBound() {
    return position.distance(rawPosition) > type.getMaxRange();
  }
}
