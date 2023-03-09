package survival2d.match.entity.item;

import lombok.Builder;
import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.ItemType;
import survival2d.match.entity.config.BulletType;

@Builder
@Getter
public class BulletItem implements Item {

  final ItemType itemType = ItemType.BULLET;
  BulletType bulletType;
  long numBullet;
}
