package survival2d.match.entity.weapon;

import lombok.Getter;
import survival2d.match.entity.Weapon;
import survival2d.match.entity.WeaponType;

@Getter
public class Hand extends Weapon {

  private final WeaponType weaponType = WeaponType.HAND;
}
