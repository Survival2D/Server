package survival2d.match.entity;

import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.HasShape;
import survival2d.match.entity.base.Movable;

public interface Player extends Movable, HasShape, HasHp {

  String getPlayerId();

  Vector2 getDirection();

  Vector2 getAttackDirection();

  double getRotation();

  void setRotation(double rotation);

//  PlayerState getState();

  double getSpeed();

  int getTeam();

  void switchWeapon(int index);

  Optional<Weapon> getCurrentWeapon();

  int getCurrentWeaponIndex();

  void reloadWeapon();

  Shape getHead();
}
