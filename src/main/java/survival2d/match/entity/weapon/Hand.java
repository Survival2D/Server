package survival2d.match.entity.weapon;

import survival2d.match.entity.Weapon;
import survival2d.match.entity.WeaponType;
import lombok.Getter;

@Getter
public class Hand extends Weapon {
  private final WeaponType weaponType = WeaponType.HAND;
}
