package survival2d.match.entity.weapon;

import survival2d.match.entity.config.WeaponType;
import lombok.Getter;

@Getter
public class Hand extends Weapon {
  private final WeaponType weaponType = WeaponType.HAND;
}
