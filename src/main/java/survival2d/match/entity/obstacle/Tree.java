package survival2d.match.entity.obstacle;

import lombok.Data;

import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.util.serialize.GsonTransient;

@Data
public class Tree implements Destroyable, HasHp, Obstacle {

  @GsonTransient
  int id;
  @GsonTransient
  double hp = 100;
  Vector2 position;
  @GsonTransient
  Circle shape;
  ObstacleType type = ObstacleType.TREE;

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  public void setDestroyed(boolean destroyed) {
  }
}
