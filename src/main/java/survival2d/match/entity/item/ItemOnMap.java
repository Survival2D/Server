package survival2d.match.entity.item;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.constant.GameConstant;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.Containable;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.quadtree.BaseMapObject;

@Builder
@Getter
@Setter
public class ItemOnMap extends BaseMapObject implements Containable, MapObject {

  private static final Circle ITEM_SHAPE = new Circle(GameConstant.ITEM_ON_MAP_RADIUS);
  final Circle shape = ITEM_SHAPE;
  int id;
  Item item;
  Vector2D position;

  @Override
  public List<Item> getItems() {
    return Collections.singletonList(item);
  }
}
