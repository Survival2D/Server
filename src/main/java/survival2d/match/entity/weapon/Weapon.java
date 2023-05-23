package survival2d.match.entity.weapon;

import survival2d.match.type.AttackType;
import survival2d.match.type.WeaponType;

public abstract class Weapon {

  public abstract WeaponType getWeaponType();

  public AttackType getAttackType() {
    return getWeaponType().getAttackType();
  }
}
