package survival2d.match.entity.obstacle;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.GsonTransient;

@Getter
@Setter
public class Stone extends BaseMapObject implements Destroyable, HasHp, Obstacle {
  @GsonTransient Circle shape = new Circle(0, 0, GameConfig.getInstance().getStoneRadius());
  double hp = GameConfig.getInstance().getStoneHp();
  ObstacleType type = ObstacleType.STONE;

  @Override
  public void setPosition(Vector2 position) {
    super.setPosition(position);
    shape.setPosition(position);
  }

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  public void setDestroyed(boolean destroyed) {}
}
