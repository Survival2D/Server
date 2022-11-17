package com.survival2d.server.game.entity.obstacle;

import com.survival2d.server.game.entity.base.Circle;
import com.survival2d.server.game.entity.base.HasHp;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class Tree implements HasHp, Obstacle {

  long id;
  double hp;
  Vector2D position;
  Circle shape;

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  public void setDestroyed(boolean destroyed) {
  }
}
