package survival2d.match.entity.weapon;

import lombok.Getter;
import survival2d.match.entity.config.WeaponType;

@Getter
public class Hand extends Weapon {
  private final WeaponType weaponType = WeaponType.HAND;
}
