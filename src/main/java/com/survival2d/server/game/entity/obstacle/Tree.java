package com.survival2d.server.game.entity.obstacle;

import com.survival2d.server.game.entity.base.Destroyable;
import com.survival2d.server.game.entity.base.HasHp;
import com.survival2d.server.game.entity.base.MapObject;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class Tree implements MapObject, HasHp, Destroyable {

  long id;
  double hp;
  Vector2D position;
  boolean isDestroyed;
}
