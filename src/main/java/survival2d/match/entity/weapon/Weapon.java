package survival2d.match.entity.weapon;

import survival2d.match.config.WeaponConfig;
import survival2d.match.type.WeaponType;

public abstract class Weapon {

  public abstract WeaponType getWeaponType();

  public abstract WeaponConfig getConfig();
}
