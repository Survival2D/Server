package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Movable;
import com.survival2d.server.game.entity.math.Vector;
import java.util.Map;

public class Bullet implements Movable {

  Vector rawPosition;
  Vector direction;

  protected Bullet(Map<String, Object> properties) {
    super(properties);
  }

  public void move() {
    moveBy(direction);
  }
}
