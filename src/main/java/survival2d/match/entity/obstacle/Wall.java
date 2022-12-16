package survival2d.match.entity.obstacle;

import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Rectangle;
import survival2d.util.serialize.ExcludeFromGson;

@Data
public class Wall implements Obstacle {

  int id;
  Vector2D position;
  @ExcludeFromGson Rectangle shape = new Rectangle(100, 100);
  ObstacleType type = ObstacleType.WALL;
}
