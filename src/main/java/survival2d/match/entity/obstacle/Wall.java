package survival2d.match.entity.obstacle;

import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Rectangle;

@Data
public class Wall implements Obstacle {

  int id;
  Vector2D position;
  Rectangle shape;
  ObstacleType type = ObstacleType.WALL;
}
