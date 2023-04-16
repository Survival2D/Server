package survival2d.match.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Data;

import survival2d.match.constant.GameConstant;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.weapon.Containable;

@Builder
@Data
public class ItemOnMap implements Containable, MapObject {

  int id;
  Item item;
  Vector2 position;
  Circle shape = new Circle(position, GameConstant.ITEM_ON_MAP_RADIUS);

  @Override
  public List<Item> getItems() {
    return Collections.singletonList(item);
  }
}
