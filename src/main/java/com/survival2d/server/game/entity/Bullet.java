package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.MapObject;
import com.survival2d.server.game.entity.base.Movable;
import com.survival2d.server.game.entity.config.BulletType;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;


@Data
public class Bullet implements MapObject, Movable {
  long id;

  Vector2D position;

  Vector2D rawPosition;
  Vector2D direction;
  BulletType type;

  public Bullet(Vector2D rawPosition, Vector2D direction, BulletType type) {
    this.rawPosition = rawPosition;
    this.position = rawPosition;
    this.direction = direction; //*speed
    this.type = type;
  }

  public void move() {
    moveBy(direction);
  }


}
