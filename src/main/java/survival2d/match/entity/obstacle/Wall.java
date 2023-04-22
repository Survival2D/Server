package survival2d.match.entity.obstacle;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Rectangle;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.ExcludeFromGson;

@Getter
@Setter
public class Wall extends BaseMapObject implements Obstacle {

  private static final Rectangle WALL_SHAPE = new Rectangle(100, 100);
  int id;
  Vector2D position;
  @ExcludeFromGson Rectangle shape = WALL_SHAPE;
  ObstacleType type = ObstacleType.WALL;
}
