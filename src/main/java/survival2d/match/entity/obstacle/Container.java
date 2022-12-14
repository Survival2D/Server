package survival2d.match.entity.obstacle;

import java.util.List;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.Rectangle;
import survival2d.match.entity.weapon.Containable;
import survival2d.util.serialize.ExcludeFromGson;
import survival2d.match.entity.base.Containable;
import java.util.List;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class Container implements Destroyable, HasHp, Obstacle, Containable {

  @ExcludeFromGson int id;
  @ExcludeFromGson double hp = 100;
  Vector2D position;
  @ExcludeFromGson Rectangle shape;
  @ExcludeFromGson List<Item> items;
  ObstacleType type = ObstacleType.CONTAINER;

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  public void setDestroyed(boolean destroyed) {}
}
