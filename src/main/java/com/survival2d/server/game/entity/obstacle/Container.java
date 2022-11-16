package com.survival2d.server.game.entity.obstacle;

import com.survival2d.server.game.entity.base.MapObject;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class Container implements MapObject {

  long id;
  Vector2D position;
}
