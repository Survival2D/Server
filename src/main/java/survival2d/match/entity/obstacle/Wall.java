package survival2d.match.entity.obstacle;

import lombok.Data;


@Data
public class Wall implements Obstacle {

  int id;
  Vector2 position;
  Rectangle shape;
  ObstacleType type = ObstacleType.WALL;
}
