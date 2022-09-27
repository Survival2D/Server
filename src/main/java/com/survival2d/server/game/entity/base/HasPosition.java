package com.survival2d.server.game.entity.base;

import com.survival2d.server.game.entity.math.Vector;

public interface HasPosition {

  Vector getPosition();

  void setPosition(Vector position);
}
