package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Movable;
import java.util.Optional;
import org.locationtech.jts.math.Vector2D;

public interface Player extends Movable {

  String getPlayerId();

  Vector2D getDirection();

  Vector2D getAttackDirection();

  double getRotation();

  void setRotation(double rotation);

  PlayerState getState();

  double getSpeed();

  long getTeam();

  void switchWeapon(int index);

  Optional<Weapon> getCurrentWeapon();

  void takeDamage(long damage);

  double getSize();
}
