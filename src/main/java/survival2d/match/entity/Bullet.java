package survival2d.match.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Movable;
import survival2d.match.entity.config.BulletType;


@Data
@Slf4j
public class Bullet implements MapObject, Movable, Destroyable {

  int id;
  String ownerId;

  Vector2 position;

  Vector2 rawPosition;
  Vector2 direction;
  BulletType type;
  boolean isDestroyed;
  Circle shape = new Circle(position, 0);

  public Bullet(String ownerId, Vector2 rawPosition, Vector2 direction, BulletType type) {
    this.ownerId = ownerId;
    this.rawPosition = rawPosition;
    this.position = rawPosition;
    this.direction = direction; //*speed
    this.type = type;
  }

  public void move() {
    moveBy(direction.scl(type.getSpeed()));
//    log.info("position: {}", position);
  }


  public boolean isOutOfBound() {
    return position.dst(rawPosition) > type.getMaxRange();
  }

  public void setPosition(Vector2 position) {
    this.position = position;
    shape.setPosition(position);
  }
}
