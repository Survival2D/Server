package survival2d.match.entity.obstacle;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.GsonTransient;

@Getter
@Setter
public class Stone extends BaseMapObject implements Obstacle {

  private static final Circle STONE_SHAPE = new Circle(100);
  int id;
  Vector2 position;
  @GsonTransient
  Circle shape = STONE_SHAPE;
  ObstacleType type = ObstacleType.STONE;
}
