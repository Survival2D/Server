package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Movable;
import com.survival2d.server.game.entity.math.Vector;

public interface Player extends Movable {

  String getName();
  Vector getDirection();
  double getRotation();
  PlayerState getState();

  void setRotation(double rotation);

  double getSpeed();
}
