package survival2d.match.entity.obstacle;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.GsonTransient;

@Getter
@Setter
public class Wall extends BaseMapObject implements Obstacle {
  @GsonTransient
  Rectangle shape =
      new Rectangle(
          0, 0, GameConfig.getInstance().getWallSize(), GameConfig.getInstance().getWallSize());

  ObstacleType type = ObstacleType.WALL;

  @Override
  public void setPosition(Vector2 position) {
    super.setPosition(position);
    shape.setPosition(position);
  }
}
