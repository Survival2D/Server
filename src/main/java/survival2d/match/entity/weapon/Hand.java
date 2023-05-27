package survival2d.match.entity.weapon;

import lombok.Getter;
import survival2d.match.config.GameConfig;
import survival2d.match.config.WeaponConfig;
import survival2d.match.type.WeaponType;

@Getter
public class Hand extends Weapon {
  private final WeaponType weaponType = WeaponType.HAND;

  @Override
  public WeaponConfig getConfig() {
    return GameConfig.getInstance().getHandConfig();
  }
}
