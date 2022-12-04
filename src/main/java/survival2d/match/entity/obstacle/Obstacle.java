package survival2d.match.entity.obstacle;


import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.MapObject;

public interface Obstacle extends Destroyable, HasHp, MapObject {

  @Override
  default boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  default void setDestroyed(boolean destroyed) {
  }
}
