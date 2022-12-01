package survival2d.game.entity;

import survival2d.game.entity.base.HasHp;
import survival2d.game.entity.base.HasShape;
import survival2d.game.entity.base.Movable;
import java.util.Optional;
import org.locationtech.jts.math.Vector2D;

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
}
