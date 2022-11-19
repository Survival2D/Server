package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Destroyable;
import com.survival2d.server.game.entity.base.Dot;
import com.survival2d.server.game.entity.base.MapObject;
import com.survival2d.server.game.entity.base.Movable;
import com.survival2d.server.game.entity.config.BulletType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.math.Vector2D;


@Data
@Slf4j
public class Bullet implements MapObject, Movable, Destroyable {

  long id;
  String playerId;

  Vector2D position;

  Vector2D rawPosition;
  Vector2D direction;
  BulletType type;
  boolean isDestroyed;
  Dot shape = new Dot();

  public Bullet(String playerId, Vector2D rawPosition, Vector2D direction, BulletType type) {
    this.playerId = playerId;
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
