package survival2d.match.entity.obstacle;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.GsonTransient;

@Getter
@Setter
public class Stone extends BaseMapObject implements Obstacle {
  int id;
  @GsonTransient Circle shape = new Circle(0, 0, GameConfig.getInstance().getStoneRadius());
  ObstacleType type = ObstacleType.STONE;

  @Override
  public void setPosition(Vector2 position) {
    super.setPosition(position);
    shape.setPosition(position);
  }
}
