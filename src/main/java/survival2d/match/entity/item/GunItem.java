package survival2d.match.entity.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.GunType;
import survival2d.match.entity.config.ItemType;
import survival2d.match.entity.config.WeaponType;

@Getter
public class GunItem implements Item {

  final ItemType itemType = ItemType.WEAPON;
  final WeaponType weaponType = WeaponType.GUN;
  GunType gunType;
  int numBullet;
}
