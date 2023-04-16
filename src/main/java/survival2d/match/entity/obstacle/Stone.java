package survival2d.match.entity.obstacle;

import lombok.Data;


@Data
public class Stone implements Obstacle {

  int id;
  Vector2 position;
  Circle shape;
  ObstacleType type = ObstacleType.STONE;
}
