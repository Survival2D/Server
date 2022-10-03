package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Movable;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

@Data
public class Bullet implements Movable {

  Vector2D position;

  Vector2D rawPosition;
  Vector2D direction;

  public Bullet(Vector2D rawPosition, Vector2D direction) {}

  public void move() {
    moveBy(direction);
  }
}
