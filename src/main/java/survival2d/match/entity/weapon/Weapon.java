package survival2d.match.entity.weapon;

import survival2d.match.entity.config.AttachType;
import survival2d.match.entity.config.WeaponType;

public abstract class Weapon {

  public abstract WeaponType getWeaponType();

  public AttachType getAttachType() {
    return getWeaponType().getAttachType();
  }
}
