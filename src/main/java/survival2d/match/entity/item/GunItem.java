package survival2d.match.entity.item;

import lombok.Builder;
import lombok.Getter;
import survival2d.match.entity.WeaponType;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.ItemType;
import survival2d.match.entity.config.GunType;

@Getter
@Builder
public class GunItem implements Item {

  final ItemType itemType = ItemType.WEAPON;
  final WeaponType weaponType = WeaponType.GUN;
  GunType gunType;
  long numBullet;
}
