package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.math.Vector;

public interface Player extends Movable {

  String getName();
  Vector getPosition();
  Vector getDirection();
  double getRotation();
  PlayerState getState();
}
