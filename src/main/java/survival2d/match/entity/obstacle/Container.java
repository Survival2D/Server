package survival2d.match.entity.obstacle;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Containable;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.Rectangle;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.ExcludeFromGson;

@Getter
@Setter
public class Container extends BaseMapObject implements Destroyable, HasHp, Obstacle, Containable {

  private static final Rectangle CONTAINER_SHAPE = new Rectangle(200, 200);
  @ExcludeFromGson int id;
  @ExcludeFromGson double hp = 100;
  Vector2D position;
  @ExcludeFromGson Rectangle shape = CONTAINER_SHAPE;
  @ExcludeFromGson List<Item> items;
  ObstacleType type = ObstacleType.CONTAINER;

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  public void setDestroyed(boolean destroyed) {}
}
