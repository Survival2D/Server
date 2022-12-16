package survival2d.match.entity.player;

import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.HasShape;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Movable;
import survival2d.match.entity.base.Shape;
import survival2d.match.entity.config.HelmetType;
import survival2d.match.entity.config.VestType;
import survival2d.match.entity.weapon.Weapon;

public interface Player extends MapObject, Movable, HasShape, HasHp {

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
  void takeItem(Item item);
  VestType getVestType();
  HelmetType getHelmetType();
}
