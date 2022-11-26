package survival2d.game.entity.item;

import survival2d.game.entity.base.Item;
import survival2d.game.entity.base.ItemType;
import survival2d.game.entity.config.BulletType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BulletItem implements Item {

  final ItemType itemType = ItemType.BULLET;
  BulletType bulletType;
  long numBullet;
}
