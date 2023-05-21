package survival2d.match.entity.obstacle;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.Containable;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.GsonTransient;

@Getter
@Setter
public class Container extends BaseMapObject implements Destroyable, HasHp, Obstacle, Containable {
  @GsonTransient int id;
  @GsonTransient double hp = 100;
  Vector2 position;

  @GsonTransient
  Rectangle shape =
      new Rectangle(
          0,
          0,
          GameConfig.getInstance().getContainerSize(),
          GameConfig.getInstance().getContainerSize());

  @GsonTransient List<Item> items;
  ObstacleType type = ObstacleType.CONTAINER;

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  public void setDestroyed(boolean destroyed) {}

  @Override
  public void setPosition(Vector2 position) {
    this.position = position;
    shape.setPosition(position);
  }
}
