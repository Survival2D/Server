package survival2d.game.entity.obstacle;

import survival2d.game.entity.base.HasHp;
import survival2d.game.entity.base.Item;
import survival2d.game.entity.base.Rectangle;
import survival2d.game.entity.weapon.Containable;
import java.util.List;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class Container implements HasHp, Obstacle, Containable {

  int id;
  double hp = 100;
  Vector2D position;
  Rectangle shape;
  List<Item> items;
  ObstacleType type = ObstacleType.CONTAINER;
}
