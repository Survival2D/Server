package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Dot;
import com.survival2d.server.game.entity.base.MapObject;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class ItemOnMap implements MapObject {
  long id;

  Vector2D position;
  Dot shape;
}
