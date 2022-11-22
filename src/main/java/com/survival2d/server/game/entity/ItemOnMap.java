package com.survival2d.server.game.entity;

import com.survival2d.server.game.constant.GameConstants;
import com.survival2d.server.game.entity.base.Circle;
import com.survival2d.server.game.entity.base.Item;
import com.survival2d.server.game.entity.base.MapObject;
import com.survival2d.server.game.entity.weapon.Containable;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Builder
@Data
public class ItemOnMap implements Containable, MapObject {

  final Circle shape = new Circle(GameConstants.ITEM_ON_MAP_RADIUS);
  long id;
  Item item;
  Vector2D position;

  @Override
  public List<Item> getItems() {
    return Collections.singletonList(item);
  }
}
