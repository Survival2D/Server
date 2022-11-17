package com.survival2d.server.game.entity.item;

import com.survival2d.server.game.entity.base.Item;
import com.survival2d.server.game.entity.base.ItemType;
import com.survival2d.server.game.entity.config.BulletType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BulletItem implements Item {

  final ItemType itemType = ItemType.BULLET;
  BulletType bulletType;
  long numBullet;
}
