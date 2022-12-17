package survival2d.match.entity.obstacle;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.ExcludeFromGson;

@Getter
@Setter
public class Tree extends BaseMapObject implements Destroyable, HasHp, Obstacle {

  @ExcludeFromGson int id;
  @ExcludeFromGson double hp = 100;
  Vector2D position;
  @ExcludeFromGson Circle shape = new Circle(50);
  @ExcludeFromGson Circle foliage = new Circle(150);
  ObstacleType type = ObstacleType.TREE;

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  public void setDestroyed(boolean destroyed) {}
}
