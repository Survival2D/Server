package survival2d.match.entity.obstacle;

import lombok.Data;
import org.locationtech.jts.math.Vector2D;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.HasHp;

@Data
public class Tree implements HasHp, Obstacle {

  int id;
  double hp = 100;
  Vector2D position;
  Circle shape;
  ObstacleType type = ObstacleType.TREE;
}
