package survival2d.match.entity.obstacle;

import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Circle;

@Data
public class Stone implements Obstacle {

  int id;
  Vector2D position;
  Circle shape;
  ObstacleType type = ObstacleType.STONE;
}
