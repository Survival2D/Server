package com.survival2d.server.game.entity.obstacle;


import com.survival2d.server.game.entity.base.Destroyable;
import com.survival2d.server.game.entity.base.HasHp;
import com.survival2d.server.game.entity.base.MapObject;

public interface Obstacle extends Destroyable, HasHp, MapObject {

  @Override
  default boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  default void setDestroyed(boolean destroyed) {
  }
}
