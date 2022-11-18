package com.survival2d.server.game.entity.obstacle;

import com.survival2d.server.game.entity.base.HasHp;
import com.survival2d.server.game.entity.base.Item;
import com.survival2d.server.game.entity.base.Rectangle;
import com.survival2d.server.game.entity.weapon.Containable;
import java.util.List;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class Container implements HasHp, Obstacle, Containable {

  long id;
  double hp;
  boolean isDestroyed;
  Vector2D position;
  Rectangle shape;
  List<Item> items;
}
