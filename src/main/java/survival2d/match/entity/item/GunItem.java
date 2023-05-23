package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.type.GunType;
import survival2d.match.type.ItemType;
import survival2d.match.type.WeaponType;

@Getter
public class GunItem implements Item {

  final ItemType itemType = ItemType.WEAPON;
  final WeaponType weaponType = WeaponType.PISTOL;
  GunType gunType;
  int numBullet;

  public GunItem(GunType gunType, int numBullet) {
    this.gunType = gunType;
    this.numBullet = numBullet;
  }
}
