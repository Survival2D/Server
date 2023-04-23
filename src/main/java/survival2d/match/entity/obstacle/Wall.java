package survival2d.match.entity.obstacle;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.GsonTransient;

@Getter
@Setter
public class Wall extends BaseMapObject implements Obstacle {

  private static final Rectangle WALL_SHAPE = new Rectangle(100, 100);
  int id;
  Vector2 position;
  @GsonTransient Rectangle shape = WALL_SHAPE;
  ObstacleType type = ObstacleType.WALL;
}
