package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Movable;
import com.survival2d.server.game.entity.math.Vector;
import lombok.Data;

@Data
public class Bullet implements Movable {

  Vector position;

  Vector rawPosition;
  Vector direction;

  public Bullet(Vector rawPosition, Vector direction) {
  }

  public void move() {
    moveBy(direction);
  }
}
