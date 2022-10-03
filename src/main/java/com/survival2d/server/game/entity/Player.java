package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Movable;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public interface Player extends Movable {

  String getPlayerId();

  Vector2D getDirection();

  double getRotation();

  void setRotation(double rotation);

  PlayerState getState();

  double getSpeed();

  long getTeam();
}
