package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.type.BulletType;
import survival2d.match.type.ItemType;

@Getter
public class BulletItem implements Item {

  final ItemType itemType = ItemType.BULLET;
  BulletType bulletType;
  int numBullet;

  public BulletItem(BulletType bulletType, int numBullet) {
    this.bulletType = bulletType;
    this.numBullet = numBullet;
  }
}
