package survival2d.match.entity;

import survival2d.match.constant.GameConstant;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.weapon.Containable;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Builder
@Data
public class ItemOnMap implements Containable, MapObject {

  final Circle shape = new Circle(GameConstant.ITEM_ON_MAP_RADIUS);
  int id;
  Item item;
  Vector2D position;

  @Override
  public List<Item> getItems() {
    return Collections.singletonList(item);
  }
}
