package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Movable;
import com.survival2d.server.game.entity.math.Vector;

public interface Player extends Movable {

  String getPlayerId();

  Vector getDirection();

  double getRotation();

  void setRotation(double rotation);

  PlayerState getState();

  double getSpeed();

  long getTeam();
}
