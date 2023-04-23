package survival2d.match.entity.weapon;

import survival2d.match.type.AttachType;
import survival2d.match.type.WeaponType;

public abstract class Weapon {

  public abstract WeaponType getWeaponType();

  public AttachType getAttachType() {
    return getWeaponType().getAttachType();
  }
}
