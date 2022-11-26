package survival2d.game.entity.obstacle;

import survival2d.game.entity.base.Circle;
import survival2d.game.entity.base.HasHp;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class Tree implements HasHp, Obstacle {

  long id;
  double hp = 100;
  Vector2D position;
  Circle shape;
  ObstacleType type = ObstacleType.TREE;
}
