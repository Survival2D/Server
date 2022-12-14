package survival2d.match.entity.player;

import org.locationtech.jts.math.Vector2D;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.HasShape;
import survival2d.match.entity.base.Movable;
import survival2d.match.entity.base.Shape;

import java.util.Optional;
import survival2d.match.entity.weapon.Weapon;

public interface Player extends Movable, HasShape, HasHp {

  String getPlayerId();

  Vector2D getDirection();

  Vector2D getAttackDirection();

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
