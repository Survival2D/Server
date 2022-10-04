package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Movable;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;


@Data
public class Bullet implements Movable {

  Vector2D position;

  Vector2D rawPosition;
  Vector2D direction;

  public Bullet(Vector2D rawPosition, Vector2D direction) {
  }

  public void move() {
    moveBy(direction);
  }
}
