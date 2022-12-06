package survival2d.match.entity.obstacle;

import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.Rectangle;
import survival2d.match.entity.weapon.Containable;
import java.util.List;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class Container implements Destroyable, HasHp, Obstacle, Containable {

  int id;
  double hp = 100;
  Vector2D position;
  Rectangle shape;
  List<Item> items;
  ObstacleType type = ObstacleType.CONTAINER;

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  public void setDestroyed(boolean destroyed) {
  }
}
