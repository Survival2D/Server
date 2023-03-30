package survival2d.match.entity.obstacle;

import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2;

@Data
public class Stone implements Obstacle {

  int id;
  Vector2 position;
  Circle shape;
  ObstacleType type = ObstacleType.STONE;
}
