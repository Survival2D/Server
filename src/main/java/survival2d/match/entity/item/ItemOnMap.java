package survival2d.match.entity.item;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.Containable;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.quadtree.BaseMapObject;

@Getter
@Setter
public class ItemOnMap extends BaseMapObject implements Containable, MapObject {
  Circle shape;
  Item item;

  public ItemOnMap(Item item, Vector2 position) {
    this.item = item;
    this.position = position;
    this.shape = new Circle(position, GameConfig.getInstance().getItemOnMapRadius());
  }

  @Override
  public List<Item> getItems() {
    return Collections.singletonList(item);
  }
}
