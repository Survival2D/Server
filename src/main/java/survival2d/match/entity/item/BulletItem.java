package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.type.GunType;
import survival2d.match.type.ItemType;

@Getter
public class BulletItem implements Item {

  final ItemType itemType = ItemType.BULLET;
  GunType gunType;
  int numBullet;

  public BulletItem(GunType gunType, int numBullet) {
    this.gunType = gunType;
    this.numBullet = numBullet;
  }
}
