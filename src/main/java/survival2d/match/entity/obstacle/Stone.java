package survival2d.match.entity.obstacle;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.ExcludeFromGson;

@Getter
@Setter
public class Stone extends BaseMapObject implements Obstacle {

  int id;
  Vector2D position;
  @ExcludeFromGson Circle shape = new Circle(100);
  ObstacleType type = ObstacleType.STONE;
}
