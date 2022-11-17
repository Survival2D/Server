package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Dot;
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

  long id;
  Item item;
  Vector2D position;
  final Dot shape = new Dot();


  @Override
  public List<Item> getItems() {
    return Collections.singletonList(item);
  }
}
