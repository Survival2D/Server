package survival2d.game.entity.item;

import survival2d.game.entity.WeaponType;
import survival2d.game.entity.base.Item;
import survival2d.game.entity.base.ItemType;
import survival2d.game.entity.config.GunType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GunItem implements Item {

  final ItemType itemType = ItemType.WEAPON;
  final WeaponType weaponType = WeaponType.GUN;
  GunType gunType;
  long numBullet;
}
