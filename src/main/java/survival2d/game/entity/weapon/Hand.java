package survival2d.game.entity.weapon;

import survival2d.game.entity.Weapon;
import survival2d.game.entity.WeaponType;
import lombok.Getter;

@Getter
public class Hand extends Weapon {
  private final WeaponType weaponType = WeaponType.HAND;
}
