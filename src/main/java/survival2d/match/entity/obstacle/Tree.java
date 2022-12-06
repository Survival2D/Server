package survival2d.match.entity.obstacle;

import lombok.Data;
import org.locationtech.jts.math.Vector2D;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;

@Data
public class Tree implements Destroyable, HasHp, Obstacle {

  int id;
  double hp = 100;
  Vector2D position;
  Circle shape;
  ObstacleType type = ObstacleType.TREE;

  @Override
  public void setDestroyed(boolean destroyed) {
  }

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }
}
