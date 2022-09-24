package com.survival2d.server.game.entity.base;

import com.survival2d.server.game.entity.Property;
import com.survival2d.server.game.entity.math.Vector;

public interface HasPosition extends MapObject {

  default Vector getPosition() {
    return (Vector) get(Property.POSITION);
  }

  default void setPosition(Vector position) {
    set(Property.POSITION, position);
  }
}
