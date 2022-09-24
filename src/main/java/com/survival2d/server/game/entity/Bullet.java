package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.AbstractMapObject;
import com.survival2d.server.game.entity.base.MapObject;
import com.survival2d.server.game.entity.math.Vector;
import java.util.Map;
import lombok.AllArgsConstructor;

public class Bullet extends AbstractMapObject implements Movable {

  Vector rawPosition;
  Vector direction;

  protected Bullet(Map<String, Object> properties) {
    super(properties);
  }

  public void move() {
    moveBy(direction);
  }
}
