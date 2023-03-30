package survival2d.match.entity.obstacle;

import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2;

@Data
public class Wall implements Obstacle {

  int id;
  Vector2 position;
  Rectangle shape;
  ObstacleType type = ObstacleType.WALL;
}
